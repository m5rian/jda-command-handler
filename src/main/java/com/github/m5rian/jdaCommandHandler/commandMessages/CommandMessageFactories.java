package com.github.m5rian.jdaCommandHandler.commandMessages;

/**
 * @author Marian
 * This class holds all {@link CommandMessageFactory} which are used in the{@link com.github.m5rian.jdaCommandHandler.CommandHandler}
 * to quickly write message with pre-made presets.
 * <p>
 * You can set these presets up in a CommandServiceBuilder, which need to be passed into a {@link com.github.m5rian.jdaCommandHandler.commandServices.ICommandService}.
 */
public class CommandMessageFactories {

    private CommandMessageFactory infoFactory;
    private CommandMessageFactory errorFactory;
    private CommandMessageFactory warningFactory;
    private CommandUsageFactory usageFactory;

    /**
     * @param infoFactory A created {@link CommandMessageFactory} for information.
     */
    public void setInfoFactory(CommandMessageFactory infoFactory) {
        this.infoFactory = infoFactory;
    }

    /**
     * @param warningFactory A created {@link CommandMessageFactory} for warnings.
     */
    public void setWarningFactory(CommandMessageFactory warningFactory) {
        this.warningFactory = warningFactory;
    }

    /**
     * @param errorFactory A created {@link CommandMessageFactory} for errors.
     */
    public void setErrorFactory(CommandMessageFactory errorFactory) {
        this.errorFactory = errorFactory;
    }

    /**
     * @param usageFactory A created {@link CommandUsageFactory} to display command usages.
     */
    public void setCommandUsageFactory(CommandUsageFactory usageFactory) {
        this.usageFactory = usageFactory;
    }

    /**
     * @return Returns the {@link CommandMessageFactory} for information.
     */
    public CommandMessageFactory getInfoFactory() {
        return infoFactory;
    }

    /**
     * @return Returns the {@link CommandMessageFactory} for warnings.
     */
    public CommandMessageFactory getWarningFactory() {
        return warningFactory;
    }

    /**
     * @return Returns the {@link CommandMessageFactory} for errors.
     */
    public CommandMessageFactory getErrorFactory() {
        return errorFactory;
    }

    /**
     * @return Returns the {@link CommandUsageFactory} to display the usage for commands.
     */
    public CommandUsageFactory getUsageFactory() {
        return usageFactory;
    }
}
