package net.avdw.todo.item;

@Deprecated
public class TodoItemTokenIdentifier {
    /**
     * Given a token identify the type.
     *
     * @param token the token to identify
     * @return the type the token represents
     */
    TodoItemTokenType identify(final String token) {
        if (token.startsWith("+") && token.length() > 1) {
            return TodoItemTokenType.PROJECT;
        } else if (token.startsWith("@") && token.length() > 1) {
            return TodoItemTokenType.CONTEXT;
        } else {
            return TodoItemTokenType.NORMAL;
        }
    }
}
