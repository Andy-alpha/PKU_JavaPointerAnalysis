package pku;


import java.io.*;
import java.util.TreeSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import pascal.taie.World;
import pascal.taie.analysis.ProgramAnalysis;
import pascal.taie.analysis.misc.IRDumper;
import pascal.taie.config.AnalysisConfig;


public class PointerAnalysisTrivial extends ProgramAnalysis<PointerAnalysisResult> {
    public static final String ID = "pku-pta-trivial";

    private static final Logger logger = LogManager.getLogger(IRDumper.class);

    /**
     * Directory to dump Result.
     */
    private final File dumpPath = new File("result.txt");

    public PointerAnalysisTrivial(AnalysisConfig config) {
        super(config);
        if (dumpPath.exists()) {
            dumpPath.delete();
        }
    }

    @Override
    public PointerAnalysisResult analyze() {
        var preprocess = new PreprocessResult();
        var result = new PointerAnalysisResult();

        World.get().getClassHierarchy().applicationClasses().forEach(jclass->{
            logger.info("Analyzing class {}", jclass.getName());
            jclass.getDeclaredMethods().forEach(method->{
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
    }

    protected void dump(PointerAnalysisResult result) {
        try (PrintStream out = new PrintStream(new FileOutputStream(dumpPath))) {
            out.println(result);
        } catch (FileNotFoundException e) {
            logger.warn("Failed to dump", e);
        }
    }

}
