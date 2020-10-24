package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.address.MainApp;
import seedu.address.commons.core.LogsCenter;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Reverts the {@code model}'s address book to its previous state.
 */
public class UndoCommand extends Command {

    public static final String COMMAND_WORD = "undo";
    public static final String MESSAGE_SUCCESS = "Undo success!";
    public static final String MESSAGE_FAILURE = "Unable to undo! No more commands that are undo-able!";
    private static final Logger logger = LogsCenter.getLogger(MainApp.class);

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        if (!model.canUndoAddressBook()) {
            throw new CommandException(MESSAGE_FAILURE);
        }

        model.undoAddressBook();

        logger.info( MESSAGE_SUCCESS + "\n" + model.getAddressBook());

        return new CommandResult(MESSAGE_SUCCESS);
    }
}