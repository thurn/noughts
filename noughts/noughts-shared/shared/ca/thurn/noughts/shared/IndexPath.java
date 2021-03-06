package ca.thurn.noughts.shared;

import org.timepedia.exporter.client.Export;
import org.timepedia.exporter.client.ExportPackage;
import org.timepedia.exporter.client.Exportable;

@Export
@ExportPackage("nts")
public class IndexPath implements Exportable {
  public static final IndexPath NOT_FOUND = new IndexPath(-1, -1);

  private final int section;
  private final int row;

  IndexPath(int section, int row) {
    this.section = section;
    this.row = row;
  }

  public int getSection() {
    return section;
  }

  public int getRow() {
    return row;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("IndexPath [section=");
    builder.append(section);
    builder.append(", row=");
    builder.append(row);
    builder.append("]");
    return builder.toString();
  }
}