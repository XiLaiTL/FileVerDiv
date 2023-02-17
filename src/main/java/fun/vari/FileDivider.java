package fun.vari;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 当前读取状态：正在读取模块、普通
 */
enum State{
    PART,NONE
}

/**
 * 读取文件，对文件进行处理
 */
public class FileDivider {
    public Map<PartModel, List<String>> map = new HashMap<>(); //存储每个版本的文本信息
    public State state = State.NONE; //存储当前读取状态
    public PartModel commonPart; //存储还未拆分时的公共部分
    public PartModel currentPart; //存储当前读取部分的版本信息
    public Path filePath; //存储本次拆分的文件
    public FileDivider(String filePathStr){
        filePath = Path.of(filePathStr);
        commonPart = new PartModel("0.0.0","common");
        map.put(commonPart,new ArrayList<>());
        currentPart = commonPart; //从公共部分开始
    }
    public void read(){
        try(var in = new BufferedReader(new FileReader(filePath.toFile()))) {
            in.lines().forEach(line->{
                var result = SentenceInterpreter.typeOneLine(line); //得到当前行的类型
                switch (state){
                    case NONE -> { //如果当前读取状态是什么也没读到
                        switch (result){
                            case BEGIN -> {state=State.PART;} //读到的行是模块开始，就让读取状态进入模块读取状态
                            default -> {map.keySet().forEach(key->map.get(key).add(line));} //读到的行是普通行，就塞进整个map里
                        }
                    }
                    case PART -> { //进入模块读取状态
                        switch (result) {
                            case BEGIN -> {}
                            case FIRST, NEXT -> { //读取到的行是版本行
                                var nowPart = SentenceInterpreter.getInfoOneLine(line); //获得行内的版本信息
                                map.put(nowPart, new ArrayList<>(map.get(commonPart))); //拷贝一份公共内容，事实上应该要拷贝低版本信息？
                                currentPart = nowPart;
                            }
                            case NONE -> {map.get(currentPart).add(line);} //读取到普通行，塞进map里
                            case END -> { //读取到模块结束
                                state = State.NONE; //设置读取状态进入什么也没读到
                                currentPart = commonPart; //当前读取部分回到公共部分
                            }
                        }
                    }
                }
            });
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
    public void write(){
        var fileName = filePath.getFileName().toString();
        var filePathParent = filePath.getParent().toString();
        for(var key:map.keySet()){
            var name =  key.fileName().isEmpty()
                    ? fileName.replace("ver",key.version()) //将xxx.ver.txt替换为xxx.7.8.9.txt
                    : fileName.replaceAll(".+ver",key.fileName()); //将xxx.ver.txt替换为 filename.txt
            var NewFilePath = Path.of(filePathParent,fileName);
            try(var out = new BufferedWriter(new BufferedWriter(new FileWriter(NewFilePath.toFile())))) {
                out.write(String.join("\n",map.get(key)));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
