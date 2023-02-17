package fun.vari;

import java.util.Arrays;
/**
 * 存储版本信息和导出时的文本名称
 */
record PartModel(String version,String fileName){ }

/**
 * 判断单行的类型：开始一个模块、首个版本行、版本行、普通行、结束一个模块
 */
enum Result{
    BEGIN,FIRST,NEXT,NONE,END
}
/**
 * 对单行进行识别
 */
public class SentenceInterpreter {
    /**
     * 标定一个行的类型，返回{@link Result}
     * @param line 单行文本
     * @return 行的类型
     */
    public static Result typeOneLine(String line){
        if(line.matches("^\\s*//main")) return Result.BEGIN; //识别到//main时，返回模块开始
        if(line.matches("^\\s*/\\*\\s*@version.+\\*/\\s*$")) return Result.FIRST; //识别到/*@version*/时，返回首个版本行
        if(line.matches("^\\s*/\\*\\s*@version.+")) return Result.NEXT; //识别到/*@version时，返回版本行
        if(line.matches("^\\s*\\*/\\s*$")) return Result.END; //识别到*/时，返回模块结束
        return Result.NONE; //返回普通行
    }

    /**
     * 获得一个版本行的信息，格式为{@link PartModel}
     * @param line 单行文本
     * @return  版本信息和导出时的文本名称
     */
    public static PartModel getInfoOneLine(String line){
        var dealLine = line.replaceAll("^\\s*/\\*\\s*","").replaceAll("\\*/\\s*$",""); //截取出@version xxx @file-name xxx格式
        var list = dealLine.split("@"); //截取出["","version xxx","file-name xxxx"]
        var version = "";
        var fileName = "";
        for(var term:list){
            var cell = term.split(" "); //截取出["version","xxx"]
            if(cell.length==2){
                if("version".equalsIgnoreCase(cell[0])){version=cell[1];}
                if("file-name".equalsIgnoreCase(cell[0])){fileName=cell[1];}
            }
        }
        return new PartModel(version,fileName);
    }
}
