# 软件分析技术 2024 课程大作业

- 作业说明 [软件分析技术（熊英飞）](https://xiongyingfei.github.io/SA_new/2024/)
- 请用 IntelliJ 打开本工程
- 请将 `src/main/java` 目录标为源代码目录
- 请将 `code` 目录标为测试代码目录
- 运行时请运行 `src/main/java/pku/PointerAnalysis.java`
- `code` 目录下存放的是样例测试数据

## 命令行参数

- 第一个参数指定代码所在目录
- 第二个参数指定 `main()` 方法所在的类
- ~~示例 `out/test/pointerAnalysis test.Hello`~~
- 示例：在 Tai-e 目录下用 gradle 运行 `gradle run --args="-a pku-pta-trivial -cp src/test/pku -m test.Hello"`

## 打包测试 jar 文件

- 在 Tai-e 目录下运行 `gradle fatJar`
- 可以在 `build/` 文件夹下找到生成的 jar 文件
- 把它迁移到工作目录 `/home/student/submission/submission.jar`
- 在工作目录下创建一个 `run.sh` ，内容为 `java -jar submission.jar -a pku-pta -cp testcase -m $1`
    - (或者直接执行 `java -jar submission.jar -a pku-pta -cp testcase -m <待测试类名>`)
- 在工作目录目录下放置太阿所需的文件（ `java-benchmarks` 文件夹）
- 在工作目录目录下放置待分析的代码（ `testcase` 文件夹，内含 `benchmark/` 和 `test/` 等）
- 运行 `./run.sh <待测试的类名>`
- 读取工作目录下的输出结果（`result.txt`）
- 将结果与标准答案对比，根据是否 Sound 及精确度打分

## 安装 & IR-Dumper（已经完成则跳过这步）

1. 下载代码，建议直接从项目包中解压
2. 在本地环境中安装OpenJDK17、Gradle
3. 在Tai-e目录下执行:
git clone https://github.com/pascal-lab/java-benchmarks
4. 在Tai-e目录下执行: `gradle run --args="-a ir-dumper -cp src/test/pku -m test.Hello"`
    - 或者执行 `java -jar tai-e-all-0.2.2.jar -a ir-dumper -cp src/test/pku -m test.Hello`
    - 如果显示successful，说明运行成功，可在output/tir下找到输出