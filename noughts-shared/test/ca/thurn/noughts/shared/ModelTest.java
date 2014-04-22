package ca.thurn.noughts.shared;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import ca.thurn.noughts.shared.entities.Action;
import ca.thurn.noughts.shared.entities.Command;
import ca.thurn.noughts.shared.entities.Game;
import ca.thurn.noughts.shared.entities.GameStatus;
import ca.thurn.noughts.shared.entities.Profile;
import ca.thurn.noughts.shared.entities.Pronoun;
import ca.thurn.testing.SharedTestCase;

import com.firebase.client.Firebase;
import com.firebase.client.Firebase.CompletionListener;
import com.firebase.client.FirebaseError;

public class ModelTest extends SharedTestCase {
  private Model model;
  private String userId;
  private String userKey;
  private Firebase firebase;

  private static abstract class TestGameUpdateListener implements GameUpdateListener {
    @Override
    public void onGameUpdate(Game game) {}
    @Override
    public void onCurrentActionUpdate(Action currentAction) {}
    @Override
    public void onGameStatusChanged(GameStatus status) {}
    @Override
    public void onProfileRequired(String gameId) {}
  }

  @Override
  public void sharedSetUp(final Runnable done) {
    firebase = new Firebase("https://noughts-test.firebaseio-demo.com");
    firebase.removeValue(new CompletionListener() {
      @Override public void onComplete(FirebaseError error, Firebase firebase) {
        userId = "id" + randomInteger();
        userKey = "K" + userId;
        model = new Model(userId, userKey, firebase);
        done.run();
      }
    });
  }

  @Override
  public void sharedTearDown() {
    model.removeAllFirebaseListeners();
  }

  public void testNewGame() {
    beginAsyncTestBlock();
    model.setGameListListener(new AbstractGameListListener() {
      @Override
      public void onGameAdded(Game game) {
        assertTrue(game.getPlayerList().contains(userId));
        assertEquals(Model.X_PLAYER, game.getCurrentPlayerNumber());
        assertTrue(game.getLastModified() > 0);
        assertTrue(game.isLocalMultiplayer());
        assertFalse(game.isGameOver());
        assertEquals(0, game.getSubmittedActionCount());
        assertEquals("John", game.getProfile(0).getName());
        finished();
      }
    });
    List<Profile> localProfiles = new ArrayList<Profile>();
    Profile profile = Profile.newBuilder().setName("John").build();
    localProfiles.add(profile);
    String id = model.newLocalMultiplayerGame(localProfiles);
    assertFalse(id.equals(""));
    endAsyncTestBlock();
  }

  public void testNewGameGameUpdate() {
    beginAsyncTestBlock();
    List<Profile> profiles = new ArrayList<Profile>();
    Profile.Builder profile = Profile.newBuilder();
    profile.setName("John");
    profile.setPronoun(Pronoun.NEUTRAL);
    profiles.add(profile.build());
    String id = model.newGame(profiles);
    model.setGameUpdateListener(id, new TestGameUpdateListener() {
      @Override
      public void onGameUpdate(Game game) {
        assertTrue(game.getPlayerList().contains(userId));
        assertEquals(Model.X_PLAYER, game.getCurrentPlayerNumber());
        assertTrue(game.getLastModified() > 0);
        assertFalse(game.isLocalMultiplayer());
        assertFalse(game.isGameOver());
        assertEquals(0, game.getSubmittedActionCount());
        assertEquals("John", game.getProfile(0).getName());
        finished();
      }
    });
    endAsyncTestBlock();
  }

  public void testAddCommandToExistingAction() {
    beginAsyncTestBlock(2);
    final Game.Builder game = newGameWithTwoPlayers();
    final Command command = Command.newDeserializer()
        .deserialize(map("column", 2, "row", 2));
    withTestData(game.build(), newEmptyAction(game.getId()), new Runnable() {
      @Override
      public void run() {
        model.setGameUpdateListener(game.getId(), new TestGameUpdateListener() {
          @Override
          public void onGameUpdate(Game update) {
            assertTrue(update.getLastModified() > 200);
            finished();
          }

          @Override
          public void onCurrentActionUpdate(Action currentAction) {
            assertEquals(0, currentAction.getFutureCommandCount());
            assertEquals(command, currentAction.getCommand(0));
            assertEquals(0, currentAction.getPlayerNumber());
            assertEquals(game.getId(), currentAction.getGameId());
            finished();
          }
        }, false /* immediate */);
        model.addCommand(game.getId(), command);
      }
    });
    endAsyncTestBlock();
  }

  public void testUpdateLastCommand() {
    beginAsyncTestBlock();
    final Game game = newGameWithTwoPlayers().build();
    Action.Builder action = newUnsubmittedActionWithCommand(game.getId());
    final Command command = Command.newBuilder().setColumn(2).setRow(2).build();
    withTestData(game, action.build(), new Runnable() {
      @Override
      public void run() {
        model.setGameUpdateListener(game.getId(), new TestGameUpdateListener() {
          @Override
          public void onCurrentActionUpdate(Action currentAction) {
            assertEquals(list(command), currentAction.getCommandList());
            finished();
          }
        }, false /* immediate */);
        model.updateLastCommand(game.getId(), command);
      }
    });
    endAsyncTestBlock();
  }

  public void testCanUpdateLastCommand() {
    Game game = newGameWithTwoPlayers().build();
    Action action = newUnsubmittedActionWithCommand(game.getId()).build();
    Command command = Command.newBuilder().setRow(1).setColumn(1).build();
    assertTrue(model.couldUpdateLastCommand(game, action, command));
  }

  public void testCannotUpdateLastCommand() {
    Game game = newGameWithTwoPlayers().build();
    Command command = Command.newBuilder().setRow(1).setColumn(1).build();
    assertFalse(model.couldUpdateLastCommand(game, newEmptyAction(game.getId()), command));
  }

  public void testAddCommandNotCurrentPlayer() {
    beginAsyncTestBlock();
    final Game game = newGameWithTwoPlayers()
        .clearPlayerList()
        .addPlayer("opponent")
        .addPlayer(userId)
        .build();
    withTestData(game, newEmptyAction(game.getId()), new Runnable() {
      @Override
      public void run() {
        assertDiesAndFinish(new Runnable() {
          @Override public void run() {
            model.addCommand(game.getId(), newCommand(0, 0));
          }
        });
      }
    });
    endAsyncTestBlock();
  }

  public void testAddCommandToNewAction() {
    beginAsyncTestBlock(2);
    final Game.Builder game = newGameWithTwoPlayers();
    final Command command = newCommand(1, 1);
    withTestData(game.build(), newEmptyAction(game.getId()), new Runnable() {
      @Override
      public void run() {
        model.setGameUpdateListener(game.getId(), new TestGameUpdateListener() {
          @Override
          public void onGameUpdate(Game update) {
            assertTrue(update.getLastModified() > 200);
            finished();
          }

          @Override
          public void onCurrentActionUpdate(Action currentAction) {
            assertEquals(0, currentAction.getPlayerNumber());
            assertFalse(currentAction.isSubmitted());
            assertEquals(game.getId(), currentAction.getGameId());
            assertEquals(1, currentAction.getCommandCount());
            assertEquals(command, currentAction.getCommand(0));
            finished();
          }
        }, false /* immediate */);
        model.addCommand(game.getId(), command);
      }
    });
    endAsyncTestBlock();
  }

  public void testAddCommandAndSubmit() {
    beginAsyncTestBlock();
    final Game.Builder game = newGameWithTwoPlayers();
    final Command command = newCommand(1, 1);
    withTestData(game.build(), newEmptyAction(game.getId()), new Runnable() {
      @Override
      public void run() {
        model.setGameUpdateListener(game.getId(), new TestGameUpdateListener() {
          final AtomicInteger numGameUpdates = new AtomicInteger(0);
          @Override
          public void onGameUpdate(Game game) {
            // Allow for two updates because addAndSubmit fires twice
            if (numGameUpdates.getAndIncrement() == 1) {
              assertEquals(command, game.getSubmittedAction(0).getCommand(0));
              assertEquals(1, game.getCurrentPlayerNumber());
              finished();
            }
          }

          @Override
          public void onCurrentActionUpdate(Action currentAction) {
          }
        }, false /* immediate */);
        model.addCommandAndSubmit(game.getId(), command);
      }
    });
    endAsyncTestBlock();
  }

  public void testCouldAddCommand() {
    Command command = newCommand(1, 1);
    Game.Builder game = newGameWithTwoPlayers();
    game.setIsGameOver(true);
    assertFalse(model.couldAddCommand(game.build(), newEmptyAction(game.getId()), command));

    game = newGameWithTwoPlayers();
    Action action2 = newUnsubmittedActionWithCommand(game.getId()).build();
    assertFalse(model.couldAddCommand(game.build(), action2, command));

    game.addSubmittedAction(Action.newBuilder()
        .setIsSubmitted(true)
        .addCommand(newCommand(2, 1)));
    assertTrue(model.couldAddCommand(game.build(), newEmptyAction(game.getId()), command));

    game.clearSubmittedActionList();
    game.addSubmittedAction(Action.newBuilder().setIsSubmitted(true).addCommand(command));
    assertFalse(model.couldAddCommand(game.build(), newEmptyAction(game.getId()), command));
  }

//  public void testCanUndo() {
//    Game.Builder game = newGameWithCurrentCommand();
//    assertTrue(model.canUndo(game.build()));
//    Action.Builder action = game.getCurrentAction().toBuilder();
//    action.clearCommandList();
//    game.setCurrentAction(action);
//    assertFalse(model.canUndo(game.build()));
//  }
//
//  public void testCanRedo() {
//    Game.Builder game = newGameWithCurrentCommand();
//    assertFalse(model.canRedo(game.build()));
//    Action.Builder action = game.getCurrentAction().toBuilder();
//    Command command = action.getCommandList().remove(0);
//    action.addFutureCommand(command);
//    game.setCurrentAction(action);
//    assertTrue(model.canRedo(game.build()));
//  }
//
//  public void testCanSubmit() {
//    Game.Builder game = newGameWithCurrentCommand();
//    assertTrue(model.canSubmit(game.build()));
//    Action.Builder action = game.getCurrentAction().toBuilder();
//    action.clearCommandList();
//    game.setCurrentAction(action);
//    assertFalse(model.canSubmit(game.build()));
//    Action.Builder action2 = game.getCurrentAction().toBuilder();
//    action2.addCommand(newCommand(0, 5));
//    game.setCurrentAction(action2);
//    assertFalse(model.canSubmit(game.build()));
//  }
//
//  public void testComputeVictors() {
//    Game.Builder game = newGame();
//    game.addAllSubmittedAction(list(
//      action(0, 0, 0, true),
//      action(1, 0, 1, true)
//    ));
//    game.setCurrentAction(action(0, 0, 2, false));
//    assertNull(model.computeVictorsIfCurrentActionSubmitted(game.build()));
//
//    game = newGame();
//    game.addAllSubmittedAction(list(
//      action(0, 0, 0, true),
//      action(0, 0, 1, true)
//    ));
//    game.setCurrentAction(action(0, 0, 2, false));
//    assertDeepEquals(list(0), model.computeVictorsIfCurrentActionSubmitted(game.build()));
//
//    game = newGame();
//    game.addAllSubmittedAction(list(
//      action(0, 0, 0, true),
//      action(0, 1, 1, true)
//    ));
//    game.setCurrentAction(action(0, 2, 2, false));
//    assertDeepEquals(list(0), model.computeVictorsIfCurrentActionSubmitted(game.build()));
//
//    game = newGame();
//    game.addAllSubmittedAction(list(
//      action(0, 0, 2, true),
//      action(1, 1, 1, true),
//      action(0, 1, 2, true),
//      action(1, 0, 1, true)
//    ));
//    game.setCurrentAction(action(0, 2, 2, false));
//    assertDeepEquals(list(0), model.computeVictorsIfCurrentActionSubmitted(game.build()));
//
//    game = newGame();
//    game.addPlayer("x");
//    game.addPlayer("o");
//    game.addAllSubmittedAction(list(
//      action(1, 0, 0, true),
//      action(0, 0, 1, true),
//      action(1, 0, 2, true),
//      action(1, 1, 0, true),
//      action(0, 1, 1, true),
//      action(1, 1, 2, true),
//      action(0, 2, 0, true),
//      action(1, 2, 1, true)
//    ));
//    game.setCurrentAction(action(0, 2, 2, false));
//    assertDeepEquals(list(0, 1), model.computeVictorsIfCurrentActionSubmitted(game.build()));
//  }
//
//  public void testSubmitCurrentAction() {
//    beginAsyncTestBlock();
//    final Game game = newGameWithCurrentCommand().build();
//    withTestData(game, new Runnable() {
//      @Override
//      public void run() {
//        model.setGameUpdateListener(game.getId(), new OnGameUpdateListener() {
//          @Override
//          public void onGameUpdate(Game game) {
//            assertEquals(1, (int)game.getCurrentPlayerNumber());
//            assertFalse(game.hasCurrentAction());
//            finished();
//          }
//        });
//        model.submitCurrentAction(game);
//      }
//    });
//    endAsyncTestBlock();
//  }
//
//  public void testSubmitCurrentActionUpdatesGameStatus() {
//    beginAsyncTestBlock();
//    final Game game = newGameWithCurrentCommand().build();
//    withTestData(game, new Runnable() {
//      @Override
//      public void run() {
//        model.setGameUpdateListener(game.getId(), new GameStatusUpdateListener() {
//          @Override
//          public void onGameUpdate(Game game) {}
//
//          @Override
//          public void onGameStatusChanged(GameStatus status) {
//            assertEquals(status.getStatusPlayer(), 1);
//            finished();
//          }
//        });
//        model.submitCurrentAction(game);
//      }
//    });
//    endAsyncTestBlock();
//  }
//
//  public void testSubmitCurrentActionGameList() {
//    beginAsyncTestBlock();
//    final Game game = newGameWithCurrentCommand().build();
//    withTestData(game, new Runnable() {
//      @Override
//      public void run() {
//        model.setGameListListener(new AbstractGameListListener() {
//          @Override
//          public void onGameChanged(Game game) {
//            assertEquals(1, (int)game.getCurrentPlayerNumber());
//            assertTrue(game.getLastModified() > 150L);
//            assertFalse(game.hasCurrentAction());
//            finished();
//          }
//        });
//        model.submitCurrentAction(game);
//      }
//    });
//    endAsyncTestBlock();
//  }
//
//  public void testSubmitCurrentActionLocalMultiplayer() {
//    beginAsyncTestBlock();
//    final Game.Builder game = newGameWithCurrentCommand();
//    game.setIsLocalMultiplayer(true);
//    game.clearPlayerList();
//    game.addPlayer(userId);
//    game.addPlayer(userId);
//    withTestData(game.build(), new Runnable() {
//      @Override
//      public void run() {
//        model.setGameUpdateListener(game.getId(), new OnGameUpdateListener() {
//          @Override
//          public void onGameUpdate(Game game) {
//            assertEquals(userId, Games.currentPlayerId(game));
//            finished();
//          }
//        });
//        model.submitCurrentAction(game.build());
//      }
//    });
//    endAsyncTestBlock();
//  }
//
//  public void testSubmitCurrentActionGameOver() {
//    beginAsyncTestBlock();
//    final Game.Builder game = newGame();
//    game.addPlayer(userId);
//    game.addPlayer("o");
//    game.setProfile(0, Profile.newDeserializer().deserialize(map(
//        "name", "User",
//        "pronoun", "MALE"
//        )));
//    game.setProfile(1, Profile.newDeserializer().deserialize(map(
//        "name", "Opponent",
//        "pronoun", "FEMALE"
//        )));
//    game.addAllSubmittedAction(list(
//      action(0, 0, 2, true),
//      action(1, 1, 1, true),
//      action(0, 1, 2, true),
//      action(1, 0, 1, true)
//    ));
//    Action.Builder action = Action.newBuilder();
//    action.setPlayerNumber(0);
//    action.setIsSubmitted(false);
//    action.addCommand(newCommand(2, 2));
//    action.setGameId(game.getId());
//    game.setCurrentAction(action);
//    game.setCurrentPlayerNumber(0);
//    withTestData(game.build(), new Runnable() {
//      @Override
//      public void run() {
//        model.setGameUpdateListener(game.getId(), new OnGameUpdateListener() {
//          @Override
//          public void onGameUpdate(Game game) {
//            assertFalse(game.hasCurrentPlayerNumber());
//            assertFalse(game.hasCurrentAction());
//            assertEquals(0, game.getVictor(0));
//            assertEquals(1, game.getVictorCount());
//            assertTrue(game.isGameOver());
//            finished();
//          }
//        });
//        model.submitCurrentAction(game.build());
//      }
//    });
//    endAsyncTestBlock();
//  }
//
//  public void testUndo() {
//    beginAsyncTestBlock();
//    final Game.Builder game = newGameWithCurrentCommand();
//    withTestData(game.build(), new Runnable() {
//      @Override
//      public void run() {
//        model.setGameUpdateListener(game.getId(), new OnGameUpdateListener() {
//          @Override
//          public void onGameUpdate(Game game) {
//            assertDeepEquals(list(), game.getCurrentAction().getCommandList());
//            assertDeepEquals(list(newCommand(2, 1)),
//                game.getCurrentAction().getFutureCommandList());
//            finished();
//          }
//        });
//        model.undoCommand(game.build());
//      }
//    });
//    endAsyncTestBlock();
//  }
//
//  public void testRedo() {
//    beginAsyncTestBlock();
//    final Game.Builder game = newGameWithCurrentCommand();
//    Action.Builder action = game.getCurrentAction().toBuilder();
//    action.clearCommandList();
//    final Command command = newCommand(0, 0);
//    action.addFutureCommand(command);
//    game.setCurrentAction(action);
//    withTestData(game.build(), new Runnable() {
//      @Override
//      public void run() {
//        model.setGameUpdateListener(game.getId(), new OnGameUpdateListener() {
//          @Override
//          public void onGameUpdate(Game game) {
//            assertDeepEquals(list(command), game.getCurrentAction().getCommandList());
//            assertDeepEquals(list(), game.getCurrentAction().getFutureCommandList());
//            finished();
//          }
//        });
//        model.redoCommand(game.build());
//      }
//    });
//    endAsyncTestBlock();
//  }
//
//  public void testResignNotPlayer() {
//    assertDies(new Runnable() {
//      @Override
//      public void run() {
//        model.resignGame(newGame(map(
//            "playerList", list("foo"),
//            "currentPlayerNumber", 0
//        )).build());
//      }
//    });
//  }
//
//  public void testResignGameOver() {
//    assertDies(new Runnable() {
//      @Override
//      public void run() {
//        model.resignGame(newGame(map(
//            "playerList", list(userId, "foo"),
//            "currentPlayerNumber", 0,
//            "gameOver", true
//        )).build());
//      }
//    });
//  }
//
//  public void testResignLocalMultiplayer() {
//    beginAsyncTestBlock();
//    final Game.Builder game = newLocalMultiplayerGameWithTwoPlayers();
//    game.setCurrentPlayerNumber(1);
//    withTestData(game.build(), new Runnable() {
//      @Override
//      public void run() {
//        model.setGameUpdateListener(game.getId(), new OnGameUpdateListener() {
//          @Override
//          public void onGameUpdate(Game game) {
//            assertDeepEquals(list(new Integer(1)), game.getResignedPlayerList());
//            assertTrue(game.isGameOver());
//            assertFalse(game.hasCurrentAction());
//            assertFalse(game.hasCurrentPlayerNumber());
//            assertTrue(game.getVictorList().contains(0));
//            assertTrue(game.getLastModified() > 150L);
//            finished();
//          }
//        });
//        model.resignGame(game.build());
//      }
//    });
//    endAsyncTestBlock();
//  }
//
//  public void testResignGame() {
//    beginAsyncTestBlock();
//    final Game.Builder game = newGameWithTwoPlayers();
//    withTestData(game.build(), new Runnable() {
//      @Override
//      public void run() {
//        model.setGameUpdateListener(game.getId(), new OnGameUpdateListener() {
//          @Override
//          public void onGameUpdate(Game game) {
//            assertDeepEquals(list(new Integer(0)), game.getResignedPlayerList());
//            assertTrue(game.isGameOver());
//            assertFalse(game.hasCurrentAction());
//            assertFalse(game.hasCurrentPlayerNumber());
//            assertTrue(game.getVictorList().contains(1));
//            assertTrue(game.getLastModified() > 150L);
//            finished();
//          }
//        });
//        model.resignGame(game.build());
//      }
//    });
//    endAsyncTestBlock();
//  }
//
//  public void testArchiveNotPlayer() {
//    assertDies(new Runnable() {
//      @Override
//      public void run() {
//        model.archiveGame(newGame(map(
//            "playerList", list("foo"),
//            "currentPlayerNumber", 0
//        )).build());
//      }
//    });
//  }
//
//  public void testArchiveGameNotOver() {
//    assertDies(new Runnable() {
//      @Override
//      public void run() {
//        model.archiveGame(newGame(map(
//            "playerList", list(userId, "foo"),
//            "currentPlayerNumber", 0,
//            "gameOver", false
//        )).build());
//      }
//    });
//  }
//
//  public void testArchiveListGame() {
//    beginAsyncTestBlock();
//    final Game.Builder game = newGame(map(
//        "currentPlayerNumber", 0,
//        "playerList", list(userId, "foobar"),
//        "gameOver", true
//        ));
//    withTestData(game.build(), new Runnable() {
//      @Override
//      public void run() {
//        model.setGameListListener(new AbstractGameListListener() {
//          @Override
//          public void onGameRemoved(Game newGame) {
//            assertEquals(game.build(), newGame);
//            finished();
//          }
//        });
//        model.archiveGame(game.build());
//      }
//    });
//    endAsyncTestBlock();
//  }
//
//  public void testIsCurrentPlayer() {
//    Map<String, Object> map = map("gameOver", true);
//    Game g1 = newGame(map).build();
//    assertFalse(model.isCurrentPlayer(g1));
//
//    Game g2 = newGame(map("currentPlayerNumber", 0, "playerList", list("fooId"))).build();
//    assertFalse(model.isCurrentPlayer(g2));
//
//    Game g3 = newGame(map(
//      "currentPlayerNumber", 1,
//      "playerList", list("fooId", userId))).build();
//    assertTrue(model.isCurrentPlayer(g3));
//
//    Game g4 = newGame(map(
//      "currentPlayerNumber", 0,
//      "playerList", list("fooId", userId))).build();
//    assertFalse(model.isCurrentPlayer(g4));
//  }
//
//  public void testEnsureIsCurrentPlayer() {
//    assertDies(new Runnable() {
//      @Override
//      public void run() {
//        model.ensureIsCurrentPlayer(newGame(map(
//          "playerList", list("foo", userId),
//          "currentPlayerNumber", 0
//        )).build());
//      }
//    });
//  }
//
//  public void testEnsureIsPlayer() {
//    assertDies(new Runnable() {
//      @Override
//      public void run() {
//        model.ensureIsPlayer(newGame(map(
//          "playerList", list("foo"),
//          "currentPlayerNumber", 0
//        )).build());
//      }
//    });
//  }

  @SuppressWarnings("unchecked")
  private Action action(int player, int column, int row, boolean submitted) {
    return Action.newBuilder()
        .setPlayerNumber(player)
        .setIsSubmitted(submitted)
        .addCommand(Command.newBuilder()
            .setColumn(column)
            .setRow(row))
        .build();
  }

  private Action newEmptyAction(String gameId) {
    return Action.newBuilder()
        .setGameId(gameId)
        .setIsSubmitted(false)
        .build();
  }

  private Action.Builder newUnsubmittedActionWithCommand(String gameId) {
    return Action.newBuilder()
        .setPlayerNumber(0)
        .setIsSubmitted(false)
        .setGameId(gameId)
        .addCommand(Command.newBuilder().setColumn(0).setRow(0));
  }

  private Game.Builder newGameWithTwoPlayers() {
    return Game.newBuilder()
        .setId("{gameId" + randomInteger() + "}")
        .setCurrentPlayerNumber(0)
        .addAllPlayer(list(userId, "opponentId"))
        .setIsGameOver(false)
        .setIsLocalMultiplayer(false)
        .addProfile(Profile.newBuilder()
            .setName("User")
            .setPronoun(Pronoun.MALE))
        .addProfile(Profile.newBuilder()
            .setName("Opponent")
            .setPronoun(Pronoun.FEMALE))
        .setLastModified(123);
  }

  private Game.Builder newLocalMultiplayerGameWithTwoPlayers() {
    return Game.newBuilder()
        .setId("game" + randomInteger())
        .setCurrentPlayerNumber(0)
        .addAllPlayer(list(userId, userId))
        .setIsGameOver(false)
        .setIsLocalMultiplayer(true)
        .addProfile(Profile.newBuilder()
            .setName("User")
            .setPronoun(Pronoun.MALE))
        .addProfile(Profile.newBuilder()
            .setName("User 2")
            .setPronoun(Pronoun.FEMALE))
        .setLastModified(123);
  }
  private void assertDiesAndFinish(Runnable testFn) {
    try {
      testFn.run();
    } catch (RuntimeException expected) {
      finished();
    }
  }

  private void withTestData(final Game game, final Action currentAction, final Runnable testFn) {
    model.setGameListListener(new AbstractGameListListener() {
      @Override
      public void onGameAdded(Game game) {
        model.setGameListListener(null);
        testFn.run();
      }
    });
    firebase.child("games").child(game.getId()).setValue(game.serialize());
    Firebase userGame = firebase.child("users").child(userKey).child("games").child(game.getId());
    userGame.child("currentAction").setValue(currentAction.serialize());
  }

  private Command newCommand(int column, int row) {
    return Command
        .newBuilder()
        .setColumn(column)
        .setRow(row)
        .build();
  }

  final static <T> List<T> list(T... objects) {
    List<T> result = new ArrayList<T>();
    for (T t : objects) {
      result.add(t);
    }
    return result;
  }

  static Map<String, Object> map(Object... objects) {
    Map<String, Object> result = new HashMap<String, Object>();
    for (int i = 0; i < objects.length; i += 2) {
      result.put(objects[i].toString(), objects[i + 1]);
    }
    return result;
  }
}
