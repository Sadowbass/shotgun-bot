package momc.bot;

public enum InteractionType {
    BUTTON, MODAL, STRING_SELECT, SLASH, ENTITY_SELECT;

    public static InteractionType getInteractionType(String interactionType) {
        if (interactionType.equalsIgnoreCase("entity")) {
            return InteractionType.ENTITY_SELECT;
        } else if (interactionType.equalsIgnoreCase("string")) {
            return InteractionType.STRING_SELECT;
        } else {
            return InteractionType.valueOf(interactionType.toUpperCase());
        }
    }
}
