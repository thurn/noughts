package com.tinlib.services;

import com.firebase.client.FirebaseError;
import com.google.common.collect.ImmutableMap;
import com.tinlib.analytics.AnalyticsService;
import com.tinlib.core.TinKeys;
import com.tinlib.core.TinMessages;
import com.tinlib.error.ErrorService;
import com.tinlib.error.TinException;
import com.tinlib.generated.Action;
import com.tinlib.generated.Command;
import com.tinlib.generated.Game;
import com.tinlib.generated.IndexCommand;
import com.tinlib.inject.Injector;
import com.tinlib.message.Bus;
import com.tinlib.time.LastModifiedService;
import com.tinlib.validator.ActionValidatorService;

public class AddCommandService {
  private final Bus bus;
  private final ErrorService errorService;
  private final AnalyticsService analyticsService;
  private final GameMutator gameMutator;
  private final LastModifiedService lastModifiedService;
  private final ActionValidatorService actionValidatorService;

  public AddCommandService(Injector injector) {
    bus = injector.get(TinKeys.BUS);
    errorService = injector.get(TinKeys.ERROR_SERVICE);
    analyticsService = injector.get(TinKeys.ANALYTICS_SERVICE);
    gameMutator = injector.get(TinKeys.GAME_MUTATOR);
    lastModifiedService = injector.get(TinKeys.LAST_MODIFIED_SERVICE);
    actionValidatorService = injector.get(TinKeys.ACTION_VALIDATOR_SERVICE);
  }

  public void addCommand(final Command command) {
    gameMutator.mutateCurrentAction(new GameMutator.ActionMutation() {
      @Override
      public void mutate(String viewerId, Action.Builder action, Game currentGame) {
        if (!actionValidatorService.canAddCommand(viewerId, currentGame, action.build(), command)) {
          throw new TinException("Can't add command '%s' to action '%s'", command, action);
        }
        int player = currentGame.getCurrentPlayerNumber();
        action.clearFutureCommandList();
        action.addCommand(command.toBuilder().setPlayerNumber(player));
        action.setPlayerNumber(player);
      }

      @Override
      public void onComplete(String viewerId, FirebaseReferences references, Action action,
          Game currentGame) {
        analyticsService.trackEvent("addCommand", ImmutableMap.of("command", command.toString()));
        lastModifiedService.updateLastModified(action.getGameId());
        bus.produce(TinMessages.COMMAND_ADDED, action.getCommand(action.getCommandCount() - 1));
      }

      @Override
      public void onError(String viewerId, FirebaseError error) {
        errorService.error("Error adding command '%s'. %s", command, error);
      }
    });
  }

  public void setCommand(final int index, final Command command) {
    gameMutator.mutateCurrentAction(new GameMutator.ActionMutation() {
      @Override
      public void mutate(String viewerId, Action.Builder action, Game currentGame) {
        if (!actionValidatorService.canSetCommand(viewerId, currentGame, action.build(), command,
            index)) {
          throw new TinException("Can't set command '%s' at index '%s' in action '%s'", command,
              index, action);
        }
        action.setCommand(index, command.toBuilder().setPlayerNumber(
            currentGame.getCurrentPlayerNumber()));
      }

      @Override
      public void onComplete(String viewerId, FirebaseReferences references, Action action,
                             Game currentGame) {
        analyticsService.trackEvent("setCommand",
            ImmutableMap.of("command", command.toString(), "index", index + ""));
        lastModifiedService.updateLastModified(action.getGameId());
        bus.produce(TinMessages.COMMAND_CHANGED, IndexCommand.newBuilder()
            .setCommand(action.getCommand(index))
            .setIndex(index)
            .build());
      }

      @Override
      public void onError(String viewerId, FirebaseError error) {
        errorService.error("Error setting command '%s' at index '%s'. %s", command, index, error);
      }
    });
  }
}