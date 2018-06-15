import java.io.IOException;

/**
 * "Main menu" of Jit, that checks user input from the console and calls corresponding methods.
 */
public class Jit {

    // add comments!
    //split big code parts
    //make Commit class??? a lot is done differently, but from the view of polymorphism... as it is it seems more reasonable
    // Node - to separate file, include ToObjectFile functionality

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
        catch (IllegalArgumentException ex) {
            commandHelp();
        }
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
