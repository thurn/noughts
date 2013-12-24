package ca.thurn.noughts.shared;

import java.util.HashMap;
import java.util.Map;

public class Command extends Entity {
  public static class CommandDeserializer extends EntityDeserializer<Command> {
    @Override
    public Command deserialize(Map<String, Object> commandMap) {
      return new Command(commandMap);
    }    
  }

  private final int column;

  private final int row;

  public Command(int column, int row) {
    this.column = column;
    this.row = row;
  }

  public Command(Map<String, Object> commandMap) {
    column = getInteger(commandMap, "column");
    row = getInteger(commandMap, "row");
  }

  @Override
  public Map<String, Object> serialize() {
    Map<String, Object> result = new HashMap<String, Object>();
    result.put("column", getColumn());
    result.put("row", getRow());
    return result;
  }

  @Override
  public String entityName() {
    return "Command";
  }

  public int getColumn() {
    return column;
  }

  public int getRow() {
    return row;
  }

}
