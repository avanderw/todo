package net.avdw.todo.domain;

import java.util.HashMap;

public enum Priority {
    A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V, W, X, Y, Z;

    private static final HashMap<Priority, Priority> PROMOTE = new HashMap<>();
    private static final HashMap<Priority, Priority> DEMOTE = new HashMap<>();

    static {
        PROMOTE.put(Priority.A, Priority.A);
        PROMOTE.put(Priority.B, Priority.A);
        PROMOTE.put(Priority.C, Priority.B);
        PROMOTE.put(Priority.D, Priority.C);
        PROMOTE.put(Priority.E, Priority.D);
        PROMOTE.put(Priority.F, Priority.E);
        PROMOTE.put(Priority.G, Priority.F);
        PROMOTE.put(Priority.H, Priority.G);
        PROMOTE.put(Priority.I, Priority.H);
        PROMOTE.put(Priority.J, Priority.I);
        PROMOTE.put(Priority.K, Priority.J);
        PROMOTE.put(Priority.L, Priority.K);
        PROMOTE.put(Priority.M, Priority.L);
        PROMOTE.put(Priority.N, Priority.M);
        PROMOTE.put(Priority.O, Priority.N);
        PROMOTE.put(Priority.P, Priority.O);
        PROMOTE.put(Priority.Q, Priority.P);
        PROMOTE.put(Priority.R, Priority.Q);
        PROMOTE.put(Priority.S, Priority.R);
        PROMOTE.put(Priority.T, Priority.S);
        PROMOTE.put(Priority.U, Priority.T);
        PROMOTE.put(Priority.V, Priority.U);
        PROMOTE.put(Priority.W, Priority.V);
        PROMOTE.put(Priority.X, Priority.W);
        PROMOTE.put(Priority.Y, Priority.X);
        PROMOTE.put(Priority.Z, Priority.Y);

        DEMOTE.put(Priority.A, Priority.B);
        DEMOTE.put(Priority.B, Priority.C);
        DEMOTE.put(Priority.C, Priority.D);
        DEMOTE.put(Priority.D, Priority.E);
        DEMOTE.put(Priority.E, Priority.F);
        DEMOTE.put(Priority.F, Priority.G);
        DEMOTE.put(Priority.G, Priority.H);
        DEMOTE.put(Priority.H, Priority.I);
        DEMOTE.put(Priority.I, Priority.J);
        DEMOTE.put(Priority.J, Priority.K);
        DEMOTE.put(Priority.K, Priority.L);
        DEMOTE.put(Priority.L, Priority.M);
        DEMOTE.put(Priority.M, Priority.N);
        DEMOTE.put(Priority.N, Priority.O);
        DEMOTE.put(Priority.O, Priority.P);
        DEMOTE.put(Priority.P, Priority.Q);
        DEMOTE.put(Priority.Q, Priority.R);
        DEMOTE.put(Priority.R, Priority.S);
        DEMOTE.put(Priority.S, Priority.T);
        DEMOTE.put(Priority.T, Priority.U);
        DEMOTE.put(Priority.U, Priority.V);
        DEMOTE.put(Priority.V, Priority.W);
        DEMOTE.put(Priority.W, Priority.X);
        DEMOTE.put(Priority.X, Priority.Y);
        DEMOTE.put(Priority.Y, Priority.Z);
        DEMOTE.put(Priority.Z, Priority.Z);
    }

    public Priority promote() {
        return PROMOTE.get(this);
    }

    public Priority demote() {
        return DEMOTE.get(this);
    }
}
