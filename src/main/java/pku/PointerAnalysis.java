package pku;


import java.io.*;                           // New import
import java.util.TreeSet;                   // New import

import org.apache.logging.log4j.LogManager; // New import
import org.apache.logging.log4j.Logger;     // New import

import pascal.taie.World;
import pascal.taie.analysis.ProgramAnalysis;
import pascal.taie.analysis.dataflow.analysis.constprop.CPFact;
import pascal.taie.analysis.dataflow.fact.DataflowResult;
import pascal.taie.ir.stmt.Stmt;            // New import
import pascal.taie.analysis.misc.IRDumper;  // New import
import pascal.taie.config.AnalysisConfig;


public class PointerAnalysis extends ProgramAnalysis<PointerAnalysisResult> {
    public static final String ID = "pku-pta";

    private static final Logger logger = LogManager.getLogger(IRDumper.class);

    /**
     * Directory to dump Result.
     */
    private final File dumpPath = new File("result.txt");

    public PointerAnalysis(AnalysisConfig config) {
        super(config);
        if (dumpPath.exists()) {
            dumpPath.delete();
        }
    }

    /*
    public PointerAnalysisResult analyze() {
        // the return value `result` is something like `line_number : alloc_number1; alloc_number2 …`
        var result = new PointerAnalysisResult();
        var preprocess = new PreprocessResult();
        pascal.taie.World world = World.get();
        // 用这里的world类型如果标注为var而不加上具体值的话，编译会报不安全
        var main = world.getMainMethod();
        // 比如运行以下测试：
        // gradle run --args="-a pku-pta -cp src/test/pku/ -m test.Hello"
        // 则`jclass`里面存的是`test.Hello`
        var jclass = main.getDeclaringClass();
        // logger.info("world: {}, main: {}, jclass", world, main, jclass);

        // TODO
        // You need to use `preprocess` like in PointerAnalysisTrivial
        // when you enter one method to collect infomation given by
        // Benchmark.alloc(id) and Benchmark.test(id, var)
        //
        // As for when and how you enter one method,
        // it's your analysis assignment to accomplish

        world.getClassHierarchy().applicationClasses().forEach(_jclass->{
            logger.info("Analyzing class {}", _jclass.getName());
            _jclass.getDeclaredMethods().forEach(method->{
                if(!method.isAbstract()) {
                    preprocess.analysis(method.getIR());
                    // 常量传播
                    DataflowResult<Stmt,CPFact> cpr = constPropagationResult(method.getIR());
                    logger.info("Got cpr: {}", cpr);
                    // 符号分析
                }
            });
        });

        var objs = new TreeSet<>(preprocess.obj_ids.values());
        // 这里的 pt 在循环相当于各个test_id的别名
        // `put()`函数用于把map对塞进result里
        preprocess.test_pts.forEach((test_id, pt)->{
            logger.info("test_id: {}; objs: {}; pt: {}", test_id, objs, pt);
            result.put(test_id, objs);
        });

        dump(result);

        return result;
        //return super.analyze();
    }
    */
    // Act as the Main function
    @Override
    public PointerAnalysisResult analyze() {
        Solver solver = new Solver();
        solver.Solve(World.get());
        return null;
    }
    @Deprecated
    protected void dump(PointerAnalysisResult result) {
        try (PrintStream out = new PrintStream(new FileOutputStream(dumpPath))) {
            out.println(result);
        } catch (FileNotFoundException e) {
            logger.warn("Failed to dump", e);
        }
    }
    @SuppressWarnings("unchecked")
    protected DataflowResult<Stmt,CPFact> constPropagationResult(pascal.taie.ir.IR ir) {
        Object const_prop_result = ir.getResult("const-prop");
        DataflowResult<Stmt,CPFact> cpr = null;
        if (const_prop_result != null) {
            logger.info("Got result from const-prop correctly");
            cpr = (pascal.taie.analysis.dataflow.fact.DataflowResult
                <pascal.taie.ir.stmt.Stmt,
                pascal.taie.analysis.dataflow.analysis.constprop.CPFact>)const_prop_result;
            //logger.info("Got cpr: {}", cpr);
        }
        else
            logger.info("Failed to get result from const-prop");
        //pascal.taie.analysis.dataflow.fact.DataflowResult
        //<pascal.taie.ir.stmt.Stmt,
        //pascal.taie.analysis.dataflow.analysis.constprop.CPFact>result;
        
        return cpr;
    }
    // For testing purposes only, you can delete it as you wish.
    @Deprecated
    protected Integer getInteger() {
        //pascal.taie.analysis.dataflow.fact.DataflowResult
        //<pascal.taie.ir.stmt.Stmt,
        //pascal.taie.analysis.dataflow.analysis.constprop.CPFact>result;
        int result=0;
        return result;
    }

}
