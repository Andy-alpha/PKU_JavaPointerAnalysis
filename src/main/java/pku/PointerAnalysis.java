package pku;


import java.io.*;                           // New import
import java.util.TreeSet;                   // New import

import org.apache.logging.log4j.LogManager; // New import
import org.apache.logging.log4j.Logger;     // New import

import pascal.taie.World;
import pascal.taie.analysis.ProgramAnalysis;
import pascal.taie.analysis.misc.IRDumper; // New import
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

    @Override
    public PointerAnalysisResult analyze() {
        // the return value `result` is something like `line_number : alloc_number1; alloc_number2 …`
        var result = new PointerAnalysisResult();
        var preprocess = new PreprocessResult();
        pascal.taie.World world = World.get();
        // 用这里的world类型如果标注为var而不加上具体值的话，编译会报不安全
        var main = world.getMainMethod();
        var jclass = main.getDeclaringClass();

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
                    var const_prop_result = method.getIR().getResult("const-prop");
                    if (const_prop_result != null) {
                        logger.info("Got result from const-prop correctly");
                        var cpr = (pascal.taie.analysis.dataflow.fact.DataflowResult
                            <pascal.taie.ir.stmt.Stmt,
                            pascal.taie.analysis.dataflow.analysis.constprop.CPFact>)const_prop_result;
                    }
                    else
                        logger.info("Failed to get result from const-prop");
                }
            });
        });

        var objs = new TreeSet<>(preprocess.obj_ids.values());

        preprocess.test_pts.forEach((test_id, pt)->{
            result.put(test_id, objs);
        });

        dump(result);

        return result;
        //return super.analyze();
    }

    protected void dump(PointerAnalysisResult result) {
        try (PrintStream out = new PrintStream(new FileOutputStream(dumpPath))) {
            out.println(result);
        } catch (FileNotFoundException e) {
            logger.warn("Failed to dump", e);
        }
    }

}
