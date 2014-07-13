package com.it.command;

import com.it.domain.Clusters;

public class InfoCommand extends Command {
  private static final long serialVersionUID = 1748350973388041584L;
  private Clusters clusters;

  public Clusters getClusters() {
    return clusters;
  }

  public void setClusters(Clusters clusters) {
    this.clusters = clusters;
  }

  @Override
  public String toString() {
    return "InfoCommand";
  }
}
