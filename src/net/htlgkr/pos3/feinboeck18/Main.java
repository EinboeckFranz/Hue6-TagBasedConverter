package net.htlgkr.pos3.feinboeck18;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

public class Main {
    private Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        Main main = new Main();
        TagBasedConverter converter = new TagBasedConverter();
        converter.convertToClearText(requireNonNull(main.readInFile(main.getFile())));
    }

    private File getFile() {
        return new File("exampleFiles/" + scanner.nextLine());
    }
    private List<String> readInFile(File file) {
        try {
            return Files.lines(file.toPath())
                    .collect(Collectors.toList());
        } catch (IOException e) {
            System.out.println("File not found");
            return null;
        }
    }
}