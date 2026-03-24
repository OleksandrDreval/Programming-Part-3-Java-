package ua.nure.ice.bookcatalog.ui.command;

public class ExitCommand implements MenuCommand {
  private final Runnable onExit;

  public ExitCommand(Runnable onExit) {
    this.onExit = onExit;
  }

  @Override
  public void execute() {
    System.out.println("Application is shutting down.");
    onExit.run();
  }

  @Override
  public String getTitle() {
    return "Exit";
  }
}
