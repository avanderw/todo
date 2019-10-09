package net.avdw.todo.item;

class TodoItemTokenIdentifier {
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
