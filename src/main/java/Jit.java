import java.io.IOException;

/**
 * "Main menu" of Jit. Calls methods, corresponding to user input from the console.
 */
public class Jit {

    public static void main(String[] args) {
        try {
            if (args.length == 0) {
                commandHelp();
                return;
            }
            JitCommand command = JitCommand.valueOf(args[0].toUpperCase());
            switch (command) {
                case INIT:
                    JitFunctionality.init();
                    break;
                case ADD:
                    if (args.length >= 2)
                        JitFunctionality.modify(args[1], ModificationType.ADDING);
                    else
                        System.out.println("You have to specify file for adding.");
                    break;
                case REMOVE:
                    if (args.length >= 2)
                        JitFunctionality.modify(args[1], ModificationType.REMOVING);
                    else
                        System.out.println("You have to specify file for removing.");
                    break;
                case COMMIT:
                    if (args.length >= 2)
                        JitFunctionality.commit (args[1]);
                    else
                        System.out.println("You have to write commit message.");
                    break;
                case CHECKOUT:
                    if (args.length >= 2)
                        JitFunctionality.checkout(args[1]);
                    else
                        System.out.println("You have to give the hash of the commit.");
            }
        }
        //if user didn't enter a valid command
        catch (IllegalArgumentException ex) {
            commandHelp();
        }
        //exceptions from all jit methods (files IO, serialization) are caught here
        catch(IOException ex) {
            System.out.println ("Something went wrong with files input and output.");
            ex.printStackTrace();
        }
        catch(ClassNotFoundException ex) {
            System.out.println ("Something went wrong with serialization.");
            ex.printStackTrace();
        }
    }

    static void commandHelp() {
        System.out.println("List of the supported commands:");
        for (JitCommand command : JitCommand.values()) {
            System.out.println(command + "\n");
        }
    }

}
