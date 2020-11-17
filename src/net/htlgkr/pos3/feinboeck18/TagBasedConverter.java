package net.htlgkr.pos3.feinboeck18;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TagBasedConverter {

    public void convertToClearText(List<String> lines) {
        //Sequential
        lines.forEach(line -> System.out.print(convertLine(line)));
        //NEW LINE
        System.out.println("\nPARALLEL");
        //PARALLEL
        convertParallel(lines);
    }

    //Parallel Converting
    private void convertParallel(List<String> lines) {
        ExecutorService pool = Executors.newFixedThreadPool(lines.size());
        List<Callable<String>> callables = new ArrayList<>();
        lines.forEach(line -> callables.add(new LineConverter(line)));
        try {
            pool.invokeAll(callables).forEach(FutureString -> {
                try {
                    System.out.println(FutureString.get());
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            });
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        pool.shutdown();
    }

    private static String convertLine(String line) {
        List<Tag> tags = new ArrayList<>();

        while(line.contains("<") && line.contains(">")) {
            Tag ofLine = new Tag(line.substring(line.indexOf('<'), line.indexOf('>')+1));
            if(ofLine.content().indexOf("/") == 1) {
               Tag withoutClosingBr = new Tag(ofLine.content().replaceFirst("/", ""));
               if(!tags.remove(withoutClosingBr)) {
                   System.out.print("\u001B[31mNo Opening Tag found to the following closing Tag:" + ofLine.content() + "\u001B[0m\n");
                   line = "";
               } else
                   line = line.replaceFirst(ofLine.content(), "\n");
            } else {
               tags.add(ofLine);
               line = line.replaceFirst(ofLine.content(), "").trim();
            }
        }
        return line;
    }

    public static record LineConverter(String line) implements Callable<String> {
        @Override
        public String call() {
            return TagBasedConverter.convertLine(line);
        }
    }
}