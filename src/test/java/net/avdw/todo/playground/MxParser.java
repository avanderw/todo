package net.avdw.todo.playground;

import org.mariuszgromada.math.mxparser.Argument;
import org.mariuszgromada.math.mxparser.Constant;
import org.mariuszgromada.math.mxparser.Expression;
import org.mariuszgromada.math.mxparser.Function;
import org.mariuszgromada.math.mxparser.mXparser;

public class MxParser {
    public static void main(String[] args) {
        Argument z = new Argument("z = 10");
        Constant a = new Constant("b = 2");
        Function p = new Function("p(a,h) = a*h/2");
        Expression e = new Expression("p(10, 2)-z*b/2", p, z, a);
        mXparser.consolePrintln(e.getExpressionString() + " = " + e.calculate());

        Constant mos = new Constant("moscow = 2");
        Constant siz = new Constant("size = 2");
        Expression exp = new Expression("moscow + size", mos, siz);
        mXparser.consolePrintln(exp.getExpressionString() + " = " + exp.calculate());
    }
}
