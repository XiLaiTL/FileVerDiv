package fun.vari;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

enum State{
    PART,NONE
}
public class FileDivider {
    public Map<PartModel, List<String>> map = new HashMap<>();
    public State state = State.NONE;
    public PartModel commonPart;
    public PartModel currentPart;
    public Path filePath;
    public FileDivider(String filePathStr){
        filePath = Path.of(filePathStr);
        commonPart = new PartModel("0.0.0","common");
        map.put(commonPart,new ArrayList<>());
        currentPart = commonPart;
    }
    public void read(){
        try(var in = new BufferedReader(new FileReader(filePath.toFile()))) {
            in.lines().forEach(line->{
                var result = SentenceInterpreter.typeOneLine(line);
                switch (state){
                    case NONE -> {
                        switch (result){
                            case BEGIN -> {state=State.PART;}
                            default -> {map.keySet().forEach(key->map.get(key).add(line));}
                        }
                    }
                    case PART -> {
                        switch (result) {
                            case BEGIN -> {}
                            case FIRST, NEXT -> {
                                var nowPart = SentenceInterpreter.getInfoOneLine(line);
                                map.put(nowPart, new ArrayList<>(map.get(currentPart)));
                                currentPart = nowPart;
                            }
                            case NONE -> {map.get(currentPart).add(line);}
                            case END -> {
                                state = State.NONE;
                                currentPart = commonPart;
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
                    ? fileName.replace("ver",key.version())
                    :fileName.replaceAll(".+ver",key.fileName());
            var NewFilePath = Path.of(filePathParent,fileName);
            try(var out = new BufferedWriter(new BufferedWriter(new FileWriter(NewFilePath.toFile())))) {
                out.write(String.join("\n",map.get(key)));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
