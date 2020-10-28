package seedu.address.logic.commands;

import static seedu.address.logic.parser.CliSyntax.PREFIX_LABEL_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG_NAME;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.label.Label;
import seedu.address.model.tag.Tag;
import seedu.address.model.tag.TagLabelEqualsKeywordPredicate;
import seedu.address.model.tag.TagName;
import seedu.address.model.tag.TagNameEqualsKeywordPredicate;

public class OpenCommand extends Command {

    public static final String COMMAND_WORD = "open";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Opens the file specified in the filepath of a tag or label. \n"
            + "This command accepts either tag or label but not both."
            + "\n\nParameters: "
            + PREFIX_TAG_NAME + "TAG_NAME or " + PREFIX_LABEL_NAME + "LABEL_NAME "
            + "\n\nExample: " + COMMAND_WORD + " "
            + PREFIX_TAG_NAME + "cs2103 ";
    public static final String OPEN_COMMAND_USAGE = COMMAND_WORD + " " + PREFIX_TAG_NAME + "<TAG_NAME>";
    public static final String MESSAGE_SUCCESS = "File opened! Tag: %1$s";
    public static final String MESSAGE_TAG_NOT_FOUND = "Tag '%s' not found!";
    public static final String MESSAGE_LABEL_NOT_FOUND = "No tag found with the label %s!";
    public static final String MESSAGE_ERROR = "Error opening %s: %s";
    public static final String MESSAGE_FILE_NOT_FOUND = "The file: %s doesn't exist.";

    private final TagName tagName;
    private final Label label;

    /**
     * Creates an OpenCommand to open the file specified in the {@code Tag}
     */
    public OpenCommand(TagName tagName) {
        assert tagName != null;
        this.tagName = tagName;
        this.label = null;
    }

    /**
     * Creates an OpenCommand to open the file specified by the {@code Label}
     */
    public OpenCommand(Label label) {
        assert label != null;
        this.tagName = null;
        this.label = label;
    }

    /**
     * Executes the command and opens the file specified by tagName.
     *
     * @param model {@code Model} which the command should operate on.
     * @throws CommandException if tagName cannot be found in model.
     */
    @Override
    public CommandResult execute(Model model) throws CommandException {
        assert label != null || tagName != null;

        List<Tag> tagsToOpen = new ArrayList<>();

        if (tagName != null) {
            // Open tag based on tag name
            // Check if tagName is in tag list
            List<Tag> tagList = model.findFilteredTagList(new TagNameEqualsKeywordPredicate(tagName));
            if (tagList.isEmpty()) {
                throw new CommandException(String.format(MESSAGE_TAG_NOT_FOUND, tagName.toString()));
            }
            assert tagList.size() == 1 : "Only one tag should be found.";
            tagsToOpen = List.of(tagList.get(0));
        } else {
            // Open tag based on label
            List<Tag> tagList = model.findFilteredTagList(new TagLabelEqualsKeywordPredicate(label));
            if (tagList.isEmpty()) {
                throw new CommandException(String.format(MESSAGE_LABEL_NOT_FOUND, label.toString()));
            }
            tagsToOpen = tagList;
        }

        // Perform open and get a set of opened tags
        Map<Tag, String> openResults = openTags(tagsToOpen);

        // Format result string
        StringBuilder sb = new StringBuilder();
        boolean hasError = false;
        for (Tag tag : tagsToOpen) {
            String message = openResults.get(tag);
            if (message.startsWith("Error")) {
                hasError = true;
            }
            sb.append(message).append("\n");
        }

        String resultMessage = sb.toString();
        // Remove last "\n"
        if (resultMessage.endsWith("\n")) {
            resultMessage = resultMessage.substring(0, resultMessage.length() - 1);
        }

        // Throw exception if some tags were not opened
        if (hasError) {
            throw new CommandException(resultMessage);
        }

        return new CommandResult(resultMessage);
    }

    /**
     * Opens all tags from a list and returns a map of runtime messages.
     */
    private Map<Tag, String> openTags(List<Tag> tags) {
        Map<Tag, String> result = new HashMap<>();

        // Open every tags in the list and store error messages
        tags.forEach(tag -> {
            File file = new File(tag.getFileAddress().value);
            try {
                Desktop.getDesktop().open(file);
                result.put(tag, String.format(MESSAGE_SUCCESS, tag));
            } catch (IOException | IllegalArgumentException e) {
                result.put(tag, String.format(MESSAGE_ERROR, tag, e.getMessage()));
            }
        });
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        } else if (other == null) {
            return false;
        } else if (!(other instanceof OpenCommand)) {
            return false;
        }
        OpenCommand o = (OpenCommand) other;

        if (this.tagName != null && o.tagName != null) {
            return this.tagName.equals(o.tagName);
        } else if (this.label != null && o.label != null) {
            return this.label.equals(o.label);
        }

        return false;
    }
}
