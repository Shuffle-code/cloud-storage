package org.example.nio;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.stream.Collectors;

public class NioExample {
    public static void main(String[] args) throws IOException {

        Path path = Paths.get("data");
        System.out.println(path.getParent());
        System.out.println(Files.exists(path));

        Path file = path.resolve("file.txt");
        System.out.println(file.toUri());
        System.out.println(path.toUri());
        System.out.println(file.toAbsolutePath());
        System.out.println(file.getParent());
        System.out.println();
        System.out.println();
        System.out.println(path.resolve("..").toAbsolutePath().subpath(0, 5));

        Path newPath = file.resolve("..").toAbsolutePath();
        System.out.println(newPath);
        System.out.println("*");

        Path root = path.resolve("..").normalize();

        Files.list(root)
                //.map(p -> p.normalize())
                .map(Path::toAbsolutePath)
                .forEach(System.out:: println);

        Files.write(file, " Hello, W....)!".getBytes(StandardCharsets.UTF_8), StandardOpenOption.APPEND);
        System.out.println("**");

        Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
           @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
               System.out.println(file);
                return super.visitFile(file, attrs);
            }
        });

//        Files.walk(root, 4)
//                    .forEach(System.out::println);
        System.out.println("***");

            List<String> filesTree = Files.walk(root, 2)
                    .map(p -> p.getFileName().toString())
                    .collect(Collectors.toList());

        System.out.println(filesTree);

        new Watcher(path);

    }
}
