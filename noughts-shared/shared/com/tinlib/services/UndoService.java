package com.tinlib.services;

import com.firebase.client.FirebaseError;
import com.tinlib.analytics.AnalyticsService;
import com.tinlib.core.TinKeys;
import com.tinlib.core.TinMessages;
import com.tinlib.error.ErrorService;
import com.tinlib.error.TinException;
import com.tinlib.generated.Action;
import com.tinlib.generated.Command;
import com.tinlib.generated.Game;
import com.tinlib.inject.Injector;
import com.tinlib.message.Bus;
import com.tinlib.time.LastModifiedService;
import com.tinlib.util.Games;

public class UndoService {
  private final Bus bus;
  private final ErrorService errorService;
  private final AnalyticsService analyticsService;
  private final GameMutator gameMutator;
  private final LastModifiedService lastModifiedService;

  public UndoService(Injector injector) {
    bus = injector.get(TinKeys.BUS);
    errorService = injector.get(TinKeys.ERROR_SERVICE);
    analyticsService = injector.get(TinKeys.ANALYTICS_SERVICE);
    gameMutator = injector.get(TinKeys.GAME_MUTATOR);
    lastModifiedService = injector.get(TinKeys.LAST_MODIFIED_SERVICE);
  }

  public boolean canUndo(String viewerId, Game game, Action currentAction) {
    if (!Games.isCurrentPlayer(viewerId, game)) return false;
    return currentAction.getCommandCount() > 0;
  }

  public boolean canRedo(String viewerId, Game game, Action currentAction) {
    if (!Games.isCurrentPlayer(viewerId, game)) return false;
    return currentAction.getFutureCommandCount() > 0;
  }

  public void undo() {
    gameMutator.mutateCurrentAction(new GameMutator.ActionMutation() {
      @Override
      public void mutate(String viewerId, Action.Builder action, Game currentGame) {
        if (!canUndo(viewerId, currentGame, action.build())) {
          throw new TinException("Can't undo in action '%s'", action);
        }
        Command command = action.getCommandList().remove(action.getCommandCount() - 1);
        action.addFutureCommand(command);
      }

      @Override
      public void onComplete(String viewerId, FirebaseReferences references, Action action,
          Game currentGame) {
        analyticsService.trackEvent("Undo");
        lastModifiedService.updateLastModified(action.getGameId());
        bus.produce(TinMessages.COMMAND_UNDO_COMPLETED,
            action.getFutureCommand(action.getFutureCommandCount() - 1));
      }

      @Override
      public void onError(String viewerId, FirebaseError error) {
        errorService.error("Error undoing command. %s", error);
      }
    });
  }

  public void redo() {
    gameMutator.mutateCurrentAction(new GameMutator.ActionMutation() {
      @Override
      public void mutate(String viewerId, Action.Builder action, Game currentGame) {
        if (!canRedo(viewerId, currentGame, action.build())) {
          throw new TinException("Can't redo in action '%s'", action);
        }
        Command command = action.getFutureCommandList().remove(action.getFutureCommandCount() - 1);
        action.addCommand(command);
      }

      @Override
      public void onComplete(String viewerId, FirebaseReferences references, Action action,
                             Game currentGame) {
        analyticsService.trackEvent("Redo");
        lastModifiedService.updateLastModified(action.getGameId());
        bus.produce(TinMessages.COMMAND_REDO_COMPLETED,
            action.getCommand(action.getCommandCount() - 1));
      }

      @Override
      public void onError(String viewerId, FirebaseError error) {
        errorService.error("Error undoing command. %s", error);
      }
    });
  }
}
