package com.tinlib.util;

import com.google.common.collect.ImmutableList;
import com.tinlib.generated.Game;
import com.tinlib.generated.GameStatus;
import com.tinlib.generated.ImageString;
import com.tinlib.generated.ImageType;
import com.tinlib.generated.Profile;
import com.tinlib.generated.Pronoun;
import com.tinlib.time.TimeService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class GamesTest {
  private static class FakeTimeService implements TimeService {
    private final long currentTime;

    public FakeTimeService(long currentTime) {
      this.currentTime = currentTime;
    }

    @Override
    public long currentTimeMillis() {
      return currentTime;
    }
  }

  @Test
  public void testHasCurrentPlayerId() {
    Game.Builder testGame = getTestGame();
    assertTrue(Games.hasCurrentPlayer(testGame.build()));
    testGame.clearCurrentPlayerNumber();
    assertFalse(Games.hasCurrentPlayer(testGame.build()));
    testGame.setCurrentPlayerNumber(100);
    assertFalse(Games.hasCurrentPlayer(testGame.build()));
  }

  @Test
  public void testCurrentPlayerId() {
    Game.Builder testGame = getTestGame();
    assertEquals("userId", Games.currentPlayerId(testGame.build()));
    testGame.clearCurrentPlayerNumber();
    try {
      Games.currentPlayerId(testGame.build());
      fail();
    } catch (IllegalArgumentException ignored) {}
  }

  @Test
  public void testGameStatus() {
    Game.Builder testGame = getTestGame();
    GameStatus status = Games.gameStatus(testGame.build());
    assertEquals("Player 1's turn", status.getStatusString());
    assertEquals(0, status.getStatusPlayer());
    assertEquals(newImageString("userIdString"), status.getStatusImageString());
  }

  @Test
  public void testGameStatusGameOver() {
    Game.Builder testGame = getTestGame();
    testGame.setIsGameOver(true);
    testGame.addVictor(1);
    GameStatus winnerStatus = Games.gameStatus(testGame.build());
    assertEquals("Player 2 won the game!", winnerStatus.getStatusString());
    assertEquals(newImageString("opponentIdString"),
        winnerStatus.getStatusImageString());
    assertEquals(1, winnerStatus.getStatusPlayer());

    testGame.addVictor(0);
    GameStatus drawStatus = Games.gameStatus(testGame.build());
    assertEquals("Game drawn.", drawStatus.getStatusString());
    assertEquals(Games.GAME_OVER_IMAGE_STRING, drawStatus.getStatusImageString());
    assertFalse(drawStatus.hasStatusPlayer());
  }

  @Test
  public void testChannelIdForViewer() {
    Game.Builder testGame = getTestGame();
    String id = Games.channelIdForViewer(testGame.build(), "userId");
    assertEquals("GgameId___0", id);
  }

  @Test
  public void testChannelIdForPlayer() {
    assertEquals("Ggameid___42", Games.channelIdForPlayer("gameid", 42));
  }

  @Test
  public void testOpponentPlayerNumber() {
    Game.Builder game = getTestGame();
    assertEquals(1, Games.opponentPlayerNumber(game.build(), "userId"));
    game.clearPlayerList();
    try {
      Games.opponentPlayerNumber(game.build(), "userId");
      fail();
    } catch (IllegalStateException ignored) {}
  }

  @Test
  public void testPlayerNumberForPlayerId() {
    Game.Builder test = Game.newBuilder().setId("one");
    test.setIsLocalMultiplayer(false);
    test.addPlayer("viewerid");
    test.addPlayer("otherid");
    assertEquals(0, Games.playerNumberForPlayerId(test.build(), "viewerid"));
  }

  @Test
  public void testOpponentProfile() {
    Game.Builder g1 = Game.newBuilder().setId("g1");
    g1.addPlayer("user");
    g1.addPlayer("user");
    g1.setIsLocalMultiplayer(true);
    Profile john = Profile.newBuilder().setName("John").build();
    g1.addProfile(john);
    try {
      Games.opponentProfile(g1.build(),("user"));
      fail();
    } catch (IllegalStateException ignored) {}

    Game.Builder g2 = Game.newBuilder().setId("g2");
    g2.addPlayer("user1");
    g2.addPlayer("user2");
    g2.addProfile(Profile.newBuilder().build());
    g2.addProfile(Profile.newBuilder().setName("Jane").build());
    g2.setIsLocalMultiplayer(false);
    assertEquals("Jane", Games.opponentProfile(g2.build(), "user1").getName());

    Game.Builder g3 = Game.newBuilder().setId("g3");
    g3.addPlayer("user1");
    g3.addPlayer("user2");
    g3.addProfile(Profile.newBuilder().build());
    g3.addProfile(john);
    g3.setIsLocalMultiplayer(false);
    assertEquals("John", Games.opponentProfile(g3.build(), "user1").getName());
  }

  @Test
  public void testVsString() {
    Game.Builder testGame = Game.newBuilder().setId("one");
    testGame.setIsLocalMultiplayer(true);
    testGame.addProfile(Profile.newBuilder());
    testGame.addProfile(Profile.newBuilder());
    assertEquals("Local Multiplayer Game", Games.vsString(testGame.build(),""));
    testGame = Game.newBuilder().setId("two");
    testGame.setIsGameOver(false);
    testGame.setIsLocalMultiplayer(false);
    testGame.addPlayer("one");
    testGame.addProfile(Profile.newBuilder());
    testGame.addProfile(Profile.newBuilder());
    assertEquals("vs. (No Opponent Yet)", Games.vsString(testGame.build(),""));
    testGame = Game.newBuilder().setId("four");
    testGame.setIsLocalMultiplayer(false);
    testGame.addPlayer("one");
    testGame.addPlayer("two");
    Profile profile = Profile.newBuilder().setName("GivenName").build();
    testGame.addProfile(Profile.newBuilder());
    testGame.addProfile(profile);
    assertEquals("vs. GivenName", Games.vsString(testGame.build(),"one"));
  }

  @Test
  public void testLastUpdatedString() {
    long currentTime = 300000000000L;
    FakeTimeService timeService = new FakeTimeService(currentTime);
    Game.Builder testGame = Game.newBuilder().setId("test");
    testGame.setIsGameOver(false);
    testGame.setIsLocalMultiplayer(false);
    testGame.setLastModified(currentTime - 157700000000L);
    assertEquals("Updated 5 years ago",
        Games.lastUpdatedString(timeService, testGame.build(), ""));
    testGame.setLastModified(currentTime - 31556952000L);
    assertEquals("Updated a year ago",
        Games.lastUpdatedString(timeService, testGame.build(), ""));
    testGame.setLastModified(currentTime - 5259490000L);
    assertEquals("Updated 2 months ago",
        Games.lastUpdatedString(timeService, testGame.build(), ""));
    testGame.setLastModified(currentTime - 1555200000L);
    assertEquals("Updated 2 weeks ago",
        Games.lastUpdatedString(timeService, testGame.build(), ""));
    testGame.setLastModified(currentTime - 432000000L);
    assertEquals("Updated 5 days ago",
        Games.lastUpdatedString(timeService, testGame.build(), ""));
    testGame.setLastModified(currentTime - 10800000L);
    assertEquals("Updated 3 hours ago",
        Games.lastUpdatedString(timeService, testGame.build(), ""));
    testGame.setLastModified(currentTime - 3600000L);
    assertEquals("Updated an hour ago",
        Games.lastUpdatedString(timeService, testGame.build(), ""));
    testGame.setLastModified(currentTime - 240000L);
    assertEquals("Updated 4 minutes ago",
        Games.lastUpdatedString(timeService, testGame.build(), ""));
    testGame.setLastModified(currentTime - 3000L);
    assertEquals("Updated 3 seconds ago",
        Games.lastUpdatedString(timeService, testGame.build(), ""));
    testGame.setLastModified(currentTime);
    assertEquals("Updated a second ago",
        Games.lastUpdatedString(timeService, testGame.build(), ""));

    testGame = Game.newBuilder().setId("test2");
    testGame.setIsGameOver(true);
    testGame.setIsLocalMultiplayer(false);
    testGame.addPlayer("viewerId");
    testGame.addPlayer("opponentId");
    testGame.addVictor(0);
    testGame.setLastModified(currentTime);
    assertEquals("You won a second ago",
        Games.lastUpdatedString(timeService, testGame.build(), "viewerId"));

    testGame = Game.newBuilder().setId("test3");
    testGame.setIsGameOver(true);
    testGame.setIsLocalMultiplayer(false);
    testGame.addPlayer("viewerId");
    testGame.addPlayer("opponentId");
    testGame.addProfile(Profile.newBuilder().build());
    testGame.addProfile(Profile.newBuilder().build());
    testGame.addVictor(1);
    testGame.setLastModified(currentTime);
    assertEquals("They won a second ago",
        Games.lastUpdatedString(timeService, testGame.build(), "viewerId"));

    testGame = Game.newBuilder().setId("test3");
    testGame.setIsGameOver(true);
    testGame.setIsLocalMultiplayer(false);
    testGame.addPlayer("viewerId");
    testGame.addPlayer("opponentId");
    testGame.addVictor(1);
    Profile.Builder opponentProfile = Profile.newBuilder();
    opponentProfile.setName("Opponent");
    opponentProfile.setPronoun(Pronoun.FEMALE);
    testGame.addProfile(Profile.newBuilder().build());
    testGame.addProfile(opponentProfile.build());
    testGame.setLastModified(currentTime);
    assertEquals("She won a second ago",
        Games.lastUpdatedString(timeService, testGame.build(), "viewerId"));

    testGame = Game.newBuilder().setId("test3");
    testGame.setIsGameOver(true);
    testGame.setIsLocalMultiplayer(false);
    testGame.addPlayer("viewerId");
    testGame.addPlayer("opponentId");
    testGame.addVictor(0);
    testGame.addVictor(1);
    testGame.setLastModified(currentTime);
    assertEquals("Game tied a second ago",
        Games.lastUpdatedString(timeService, testGame.build(), "viewerId"));
  }

  @Test
  public void testImageList() {
    Game.Builder testGame = getTestGame();
    testGame.setIsLocalMultiplayer(false);
    ImageString string = newImageString("opponentIdString");
    assertEquals(ImmutableList.of(string), Games.imageList(testGame.build(), "userId"));
  }

  @Test
  public void testImageListLocalMultiplayer() {
    Game.Builder testGame = getTestGame().clearProfileList();
    testGame.setIsLocalMultiplayer(true);
    ImageString image1 = newImageString("one");
    testGame.addProfile(
        Profile.newBuilder().setName("Alpha").setImageString(image1).build());
    ImageString image2 = newImageString("two");
    testGame.addProfile(
        Profile.newBuilder().setName("Beta").setImageString(image2).build());
    assertEquals(ImmutableList.of(image1, image2), Games.imageList(testGame.build(), "userId"));
  }

  @Test
  public void testImageListNoOpponent() {
    Game.Builder testGame = Game.newBuilder().setId("id");
    testGame.setIsLocalMultiplayer(false);
    testGame.addPlayer("userId");
    testGame.addProfile(Profile.newBuilder());
    testGame.addProfile(Profile.newBuilder());
    assertEquals(ImmutableList.of(Games.NO_OPPONENT_IMAGE_STRING),
        Games.imageList(testGame.build(),"userId"));
  }

  @Test
  public void testCompareGames() {
    Game.Builder g1 = Game.newBuilder().setId("one");
    Game.Builder g2 = Game.newBuilder().setId("two");
    g1.setLastModified(100L);
    g2.setLastModified(200L);
    assertTrue(Games.compareGames(g1.build(), (g2.build())) > 0);
  }

  private Game.Builder getTestGame() {
    return Game.newBuilder()
        .setId("gameId")
        .addPlayer("userId")
        .addPlayer("opponentId")
        .setCurrentPlayerNumber(0)
        .setIsLocalMultiplayer(false)
        .setIsGameOver(false)
        .addProfile(Profile.newBuilder()
            .setPronoun(Pronoun.FEMALE)
            .setImageString(ImageString.newBuilder()
                .setString("userIdString")
                .setType(ImageType.LOCAL))
            .setName("Player 1"))
        .addProfile(Profile.newBuilder()
            .setPronoun(Pronoun.NEUTRAL)
            .setImageString(ImageString.newBuilder()
                .setString("opponentIdString")
                .setType(ImageType.LOCAL))
            .setName("Player 2"));
  }

  private ImageString newImageString(String name) {
    return ImageString.newBuilder().setString(name).setType(ImageType.LOCAL).build();
  }
}