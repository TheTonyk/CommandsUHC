package com.thetonyk.UHC.Utils;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.imageio.ImageIO;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.JSONArray;
import org.json.JSONObject;

import com.thetonyk.UHC.Main;
import com.thetonyk.UHC.Features.LobbyItems;

import net.dean.jraw.ApiException;
import net.dean.jraw.RedditClient;
import net.dean.jraw.http.NetworkException;
import net.dean.jraw.http.UserAgent;
import net.dean.jraw.http.oauth.Credentials;
import net.dean.jraw.http.oauth.OAuthData;
import net.dean.jraw.http.oauth.OAuthException;
import net.dean.jraw.http.oauth.OAuthHelper;
import net.dean.jraw.managers.AccountManager;
import net.dean.jraw.managers.CaptchaHelper;
import net.dean.jraw.models.Captcha;
import net.dean.jraw.models.Submission;
import twitter4j.AsyncTwitter;
import twitter4j.AsyncTwitterFactory;
import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.TwitterAdapter;
import twitter4j.TwitterException;
import twitter4j.TwitterListener;
import twitter4j.TwitterMethod;
import twitter4j.conf.ConfigurationBuilder;

public class MatchesUtils {
	
	private static RedditClient reddit = null;
	private static CaptchaHelper captcha = null;
	private static AccountManager manager = null;
	
	public static void getUpcomingMatches(MatchesCallback<List<Match>> callback) {
	
		new BukkitRunnable() {
			
			public void run() {
				
				String rawJson = null;
				
				try {
					
					URL url = new URL("https://c.uhc.gg/api/v2/r/uhcmatches");
					BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
					StringBuffer buffer = new StringBuffer();
					int read;
					char[] chars = new char[1024];
					
					while ((read = reader.read(chars)) != -1) {
						
						buffer.append(chars, 0, read);
						
					}
					
					rawJson = buffer.toString();
					
					if (reader != null) reader.close();
					
				} catch (MalformedURLException exception) {
					
					callback.onFailure(exception);
					
				} catch (IOException exception) {
					
					callback.onFailure(exception);
					
				}
				
				List<Match> matches = new ArrayList<Match>();
				long time = new Date().getTime() / 1000;
				JSONArray json = new JSONArray(rawJson);
		
				for (int i = 0; i < json.length(); i++) {
					
					JSONObject match = json.getJSONObject(i);
					
					if (!match.getString("region").equalsIgnoreCase("EU")) continue;
					
					if (match.getLong("opens") < time) continue;
					
					String[] scenarios = new String[match.getJSONArray("gamemodes").length()];
					
					for (int y = 0; y < scenarios.length; y++) {
						
						scenarios[y] = match.getJSONArray("gamemodes").getString(y);
						
					}
					
					matches.add(new Match(match.getLong("opens"), scenarios, match.getInt("teamSize")));
					
				}
				
				callback.onSuccess(matches);
				
			}
		
		}.runTaskAsynchronously(Main.uhc);
	
	}
	
	private static void getReddit(MatchesCallback<RedditClient> callback) {
		
		new BukkitRunnable() {
			
			public void run() {
		
				if (reddit == null) reddit = new RedditClient(UserAgent.of("desktop", "com.thetonyk.UHC", "v0.1", "TheTonyk"));
				
				if (reddit.isAuthenticated()) {
					
					callback.onSuccess(reddit);
					return;
					
				}
				
				Credentials credentials = Credentials.script(Main.uhc.getConfig().getString("RedditUser"), Main.uhc.getConfig().getString("RedditPass"), Main.uhc.getConfig().getString("RedditID"), Main.uhc.getConfig().getString("RedditSecret"));
				OAuthHelper helper = reddit.getOAuthHelper();
				OAuthData data = null;
				
				try {
					
					data = helper.easyAuth(credentials);
					
				} catch (NetworkException | OAuthException exception) {
					
					callback.onFailure(exception);
					return;
					
				}
				
				reddit.authenticate(data);
				callback.onSuccess(reddit);
		
			}
			
		}.runTaskAsynchronously(Main.uhc);
		
	}
	
	private static void getCaptcha(RedditClient reddit, MatchesCallback<CaptchaHelper> callback) {
		
		new BukkitRunnable() {
			
			public void run() {
				
				if (captcha == null || !captcha.getRedditClient().equals(reddit)) captcha = new CaptchaHelper(reddit);
				
				callback.onSuccess(captcha);
			
			}
			
		}.runTaskAsynchronously(Main.uhc);
		
	}
	
	private static void getManager(RedditClient reddit, MatchesCallback<AccountManager> callback) {
		
		new BukkitRunnable() {
			
			public void run() {
				
				if (manager == null || !manager.getRedditClient().equals(reddit)) manager = new AccountManager(reddit);
				
				callback.onSuccess(manager);
			
			}
			
		}.runTaskAsynchronously(Main.uhc);
		
	}
	
	public static void postTweet(String consumerKey, String consumerSecret, String accessToken, String accessTokenSecret, String tweet, MatchesCallback<URL> callback) {
		
		ConfigurationBuilder builder = new ConfigurationBuilder();
		builder.setOAuthConsumerKey(consumerKey);
		builder.setOAuthConsumerSecret(consumerSecret);
		builder.setOAuthAccessToken(accessToken);
		builder.setOAuthAccessTokenSecret(accessTokenSecret);
		AsyncTwitter twitter = new AsyncTwitterFactory(builder.build()).getInstance();
		
		TwitterListener listener = new TwitterAdapter() {
			
			@Override
			public void updatedStatus(Status status) {
				
				URL url = null;
				
				try {
					
					url = new URL("http://twitter.com/" + status.getUser().getScreenName() + "/status/" + status.getId());
					
				} catch (MalformedURLException exception) {
					
					callback.onFailure(exception);
					twitter.shutdown();
					return;
					
				};
				
				callback.onSuccess(url);
				twitter.shutdown();
				
			}
			
			@Override
			public void onException(TwitterException exception, TwitterMethod method) {
				
				callback.onFailure(exception);
				twitter.shutdown();
				
			}
			
		};
		
		twitter.addListener(listener);
		StatusUpdate status = new StatusUpdate(tweet);
		twitter.updateStatus(status);
		
	}
	
	public static class Submit {
		
		private AccountManager.SubmissionBuilder submission;
		private RedditClient client;
		private CaptchaHelper helper;
		private AccountManager manager;
		private Captcha captcha;
		private Listener listener;
		private Player player;
	
		public Submit(Player player, String title, String text, String subreddit, MatchesCallback<URL> submitCallback) {
			
			if (player == null) return;
			
			this.player = player;
			
			this.player.sendMessage(Main.PREFIX + "Connecting to Reddit...");
			
			getReddit(new MatchesCallback<RedditClient>() {

				@Override
				public void onSuccess(RedditClient reddit) {
					
					client = reddit;
					
					getCaptcha(client, new MatchesCallback<CaptchaHelper>() {

						@Override
						public void onSuccess(CaptchaHelper done) {
							
							helper = done;
							
							getManager(client, new MatchesCallback<AccountManager>() {

								@Override
								public void onSuccess(AccountManager done) {
									
									manager = done;
									submission = new AccountManager.SubmissionBuilder(text, subreddit, title);
									captcha = null;
									
									if (!helper.isNecessary()) {
										
										player.sendMessage(Main.PREFIX + "No need Captcha this time.");
										
										sendSubmission(manager, submission, null, null, new MatchesCallback<URL>() {

											@Override
											public void onSuccess(URL url) {
												
												submitCallback.onSuccess(url);
												
											}

											@Override
											public void onFailure(Throwable exception) {
												
												submitCallback.onFailure(exception);
												
											}
											
										});
										
										return;
										
									}
									
									getNewCaptcha(helper, new MatchesCallback<Captcha>() {

										@Override
										public void onSuccess(Captcha done) {
											
											captcha = done;
											player.sendMessage(Main.PREFIX + "Please, type the Captcha in chat.");
											player.getInventory().setHeldItemSlot(0);
									
										}

										@Override
										public void onFailure(Throwable exception) {
											
											submitCallback.onFailure(exception);
											
										}
										
									});
									
								}

								@Override
								public void onFailure(Throwable exception) {
									
									submitCallback.onFailure(exception);
									
								}
								
							});
					
						}

						@Override
						public void onFailure(Throwable exception) {
							
							submitCallback.onFailure(exception);
							
						}
						
					});
			
				}

				@Override
				public void onFailure(Throwable exception) {
					
					submitCallback.onFailure(exception);
					
				}
				
			});
			
			listener = new Listener() {
				
				@EventHandler
				public void onClick(InventoryClickEvent event) {
					
					if (!(event.getWhoClicked() instanceof Player)) return;
					
					Player clicker = (Player) event.getWhoClicked();
					
					if (!clicker.equals(player)) return;
					
					event.setCancelled(true);
					
					ItemStack item = event.getCurrentItem();
					
					if (item == null || !item.hasItemMeta() || !item.getItemMeta().hasDisplayName()) return;
					
					if (item.getItemMeta().getDisplayName().equals("§2New Captcha §7(Right-Click)")) {
						
						player.sendMessage(Main.PREFIX + "Getting new Captcha...");
						
						getNewCaptcha(helper, new MatchesCallback<Captcha>() {

							@Override
							public void onSuccess(Captcha done) {
								
								captcha = done;
								player.sendMessage(Main.PREFIX + "New Captcha updated.");
								
							}

							@Override
							public void onFailure(Throwable exception) {
								
								submitCallback.onFailure(exception);
								close();
								
							}
							
						});
						
						return;
						
					}
					
					if (item.getItemMeta().getDisplayName().equals("§cCancel §7(Right-Click)")) {
						
						close();
						submitCallback.onFailure(null);
						player.sendMessage(Main.PREFIX + "Post to Reddit canceled.");
						return;
						
					}
					
				}
				
				@EventHandler
				public void onRightClick(PlayerInteractEvent event) {
					
					if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
					
					if (!event.getPlayer().equals(player)) return;
					
					ItemStack item = event.getItem();
					
					if (item == null || !item.hasItemMeta() || !item.getItemMeta().hasDisplayName()) return;
					
					if (item.getItemMeta().getDisplayName().equals("§2New Captcha §7(Right-Click)")) {
						
						player.sendMessage(Main.PREFIX + "Getting new Captcha...");
						
						getNewCaptcha(helper, new MatchesCallback<Captcha>() {

							@Override
							public void onSuccess(Captcha done) {
								
								captcha = done;
								player.sendMessage(Main.PREFIX + "New Captcha updated.");
								
							}

							@Override
							public void onFailure(Throwable exception) {
								
								submitCallback.onFailure(exception);
								close();
								
							}
							
						});
						
						return;
						
					}
					
					if (item.getItemMeta().getDisplayName().equals("§cCancel §7(Right-Click)")) {
						
						close();
						submitCallback.onFailure(null);
						player.sendMessage(Main.PREFIX + "Post to Reddit canceled.");
						return;
						
					}
					
				}
				
				@EventHandler
				public void onDrop(PlayerDropItemEvent event) {
					
					if (!event.getPlayer().equals(player)) return;
					
					event.setCancelled(true);
					
				}
				
				@EventHandler
				public void onLeave(PlayerQuitEvent event) {
					
					if (!event.getPlayer().equals(player)) return;
					
					close();
					submitCallback.onFailure(null);
					
				}
				
				@EventHandler(priority = EventPriority.LOWEST)
				public void onChat(AsyncPlayerChatEvent event) {
					
					if (!event.getPlayer().equals(player)) {
						
						if (event.getRecipients().contains(player)) event.getRecipients().remove(player);
						return;
						
					}
					
					event.setCancelled(true);
					
					sendSubmission(manager, submission, captcha, event.getMessage(), new MatchesCallback<URL>() {

						@Override
						public void onSuccess(URL url) {
							
							submitCallback.onSuccess(url);
							close();
							
						}

						@Override
						public void onFailure(Throwable exception) {
							
							if (exception instanceof ApiException) {
								
								ApiException error = (ApiException) exception;
								
								if (error.getReason().equals("BAD_CAPTCHA")) {
									
									player.sendMessage(Main.PREFIX + "Invalid captcha. Try again.");
									return;
									
								}
								
							}
							
							close();
							submitCallback.onFailure(exception);

						}
						
					});
					
				}
				
			};
			
			Bukkit.getPluginManager().registerEvents(listener, Main.uhc);
			
		}
		
		private void close() {
			
			HandlerList.unregisterAll(this.listener);
			this.player.getInventory().clear();
			
			if (this.player.getWorld().getName().equals("lobby")) LobbyItems.giveItems(this.player);
			
			
		}
		
		private void getNewCaptcha(CaptchaHelper helper, MatchesCallback<Captcha> callback) {
			
			new BukkitRunnable() {
			
				@SuppressWarnings("deprecation")
				public void run() {
					
					MapView map = null;
					
					try {
						
						Captcha captcha = helper.getNew();
						URLConnection connection = captcha.getImageUrl().openConnection();
						connection.setRequestProperty("User-Agent", "CommandsPVP 0.1");
						connection.connect();
						
						if (connection instanceof HttpURLConnection) {
							
							HttpURLConnection httpConnection = (HttpURLConnection) connection;
							int httpCode = httpConnection.getResponseCode();
							
							if ((int) Math.floor(httpCode / 100) != 2) {
								
								callback.onFailure(new IOException("HTTP Error: " + httpCode + " " + httpConnection.getResponseMessage()));
								return;
								
							}
							
						}
						
						BufferedImage image = ImageIO.read(connection.getInputStream());
						
						if (image == null) {
							
							callback.onFailure(new IOException("The image is not valid."));
							return;
							
						}
						
						if (player.getInventory().getItem(0) != null) map = Bukkit.getMap(player.getInventory().getItem(0).getDurability());
						
						if (map == null) map = Bukkit.createMap(Bukkit.getWorlds().get(0));
						
						for (MapRenderer renderer : map.getRenderers()) {
							
							map.removeRenderer(renderer);
							
						}
						
						map.addRenderer(new ImageMapRenderer(image));
						callback.onSuccess(captcha);
					
					} catch (NetworkException | ApiException | IOException exception) {
						
						callback.onFailure(exception);
						return;
						
					}
					
					final MapView mapView = map;
					
					new BukkitRunnable() {
					
						public void run() {
						
							player.getInventory().clear();
							
							ItemStack newCaptcha = ItemsUtils.createItem(Material.STAINED_CLAY, "§2New Captcha §7(Right-Click)", 1, 5);
							ItemStack cancel = ItemsUtils.createItem(Material.STAINED_CLAY, "§cCancel §7(Right-Click)", 1, 14);
							
							player.getInventory().setItem(7, newCaptcha);
							player.getInventory().setItem(8, cancel);
							
							ItemStack item = ItemsUtils.createItem(Material.MAP, "§c§lCaptcha", 1, mapView.getId());
							item = ItemsUtils.hideFlags(item);
							player.sendMap(mapView);
							player.getInventory().setItem(0, item);
					
						}
					
					}.runTask(Main.uhc);
			
				}
				
			}.runTaskAsynchronously(Main.uhc);
			
		}
		
		private void sendSubmission(AccountManager manager, AccountManager.SubmissionBuilder submission, Captcha captcha, String response, MatchesCallback<URL> callback) {
			
			new BukkitRunnable() {
				
				public void run() {
			
					Submission post;
					
					try {
						
						post = captcha == null ? manager.submit(submission) : manager.submit(submission, captcha, response);
						
					} catch (NetworkException | ApiException exception) {
						
						callback.onFailure(exception);
						return;
						
					}
					
					URL url;
					
					try {
						
						url = new URL(post.getShortURL());
						
					} catch (MalformedURLException exception) {
						
						callback.onFailure(exception);
						return;
						
					}
					
					callback.onSuccess(url);
			
				}
				
			}.runTaskAsynchronously(Main.uhc);
			
		}
	
	}
	
	public static class ImageMapRenderer extends MapRenderer {
		
	    private Image image = null;
	    
	    public ImageMapRenderer(BufferedImage image) {
	    	
	    	int zPos = 0;
	    	
	    	if (image.getHeight() < 128) zPos = (128 - image.getHeight()) / 2;
	    	
	    	BufferedImage resized = new BufferedImage(128, 128, image.getType() < 1 ? BufferedImage.TYPE_INT_ARGB : image.getType());
	    	Graphics2D graphic = resized.createGraphics();
	    	graphic.drawImage(image, 0, zPos, 128, image.getHeight(), null);
	    	graphic.dispose();
	    	graphic.setComposite(AlphaComposite.Src);
	    	graphic.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
	    	graphic.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
	    	graphic.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	    	
	    	this.image = resized.getSubimage(0, 0, 128, 128);
	    	
	    }
	    
	    @Override
	    public void render(MapView view, MapCanvas canvas, Player player) {
	        
	    	if (this.image == null) return;
	    	
	    	canvas.drawImage(0, 0, image);
	    	
	    	this.image = null;

	    }
	    
	}

	public interface MatchesCallback<T> {
		
		void onSuccess(T done);
		void onFailure(Throwable exception);
		
	}
	
	public static class Match {
	
		private long time;
		private String[] scenarios;
		private int teamSize;
		
		public Match(long time, String[] scenarios, int teamSize) {
			
			this.time = time;
			this.scenarios = scenarios;
			this.teamSize = teamSize;
			
		}
		
		public long getTime() {
			
			return this.time;
			
		}

		public String[] getScenarios() {
			
			return this.scenarios;
			
		}
		
		public int getTeamSize() {
			
			return this.teamSize;
			
		}
		
	}
	
}
