package fun.vari;

import java.util.Arrays;

record PartModel(String version,String fileName){ }

enum Result{
    BEGIN,FIRST,NEXT,NONE,END
}
public class SentenceInterpreter {
    public static Result typeOneLine(String line){
        if(line.matches("^\\s*//main")) return Result.BEGIN;
        if(line.matches("^\\s*/\\*\\s*@version.+\\*/\\s*$")) return Result.FIRST;
        if(line.matches("^\\s*/\\*\\s*@version.+")) return Result.NEXT;
        if(line.matches("^\\s*\\*/\\s*$")) return Result.END;
        return Result.NONE;
    }

    public static PartModel getInfoOneLine(String line){
        var dealLine = line.replaceAll("^\\s*/\\*\\s*","").replaceAll("\\*/\\s*$","");
        var list = dealLine.split("@");
        var version = "";
        var fileName = "";
        for(var term:list){
            var cell = term.split(" ");
            if(cell.length==2){
                if("version".equalsIgnoreCase(cell[0])){version=cell[1];}
                if("file-name".equalsIgnoreCase(cell[0])){fileName=cell[1];}
            }
        }
        return new PartModel(version,fileName);
    }
}
