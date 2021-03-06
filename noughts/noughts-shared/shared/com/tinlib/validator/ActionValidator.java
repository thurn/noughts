package com.tinlib.validator;

import com.tinlib.generated.Action;
import com.tinlib.generated.Command;
import com.tinlib.generated.Game;

public interface ActionValidator {
  public boolean canAddCommand(String viewerId, Game game, Action currentAction, Command command);

  public boolean canSetCommand(String viewerId, Game game, Action currentAction, Command command,
      int index);

  public boolean canSubmitAction(String viewerId, Game game, Action currentAction);
}
