package com.machinecoding.FileSystemQuestion;

import java.util.*;

class FileSystem{

    Node root;

    public FileSystem() {
        root = new Node(false);
    }
    public void mkdir(String path){

        if(path == null || path.isEmpty() || path.equals("/")) {
            throw new IllegalArgumentException("Invalid path");
        }

        String[] parts = path.split("/");
        Node current = root;

        for(int i = 1; i < parts.length - 1; i++){
            String part = parts[i];
            if(!current.children.containsKey(part) || current.children.get(part).isFile ){
                throw new IllegalArgumentException("Parent Directory doesn't exist");
            }
            current = current.children.get(part);
        }

        String dirName = parts[parts.length - 1];
        if(current.children.containsKey(dirName)){
            throw new IllegalArgumentException("Directory already exists" + dirName);
        }

        current.children.put(dirName, new Node(false));

    }
    public String readFile(String path){

        if(path == null || path.isEmpty() || path.endsWith("/")) {
            throw new IllegalArgumentException("Invalid path");
        }

        Node current = root;
        String[] parts = path.split("/");

        for(int i = 1; i < parts.length; i++){
            String part = parts[i];
            if(!current.children.containsKey(part)){
                throw new IllegalArgumentException("File doesn't exist");
            }
            current = current.children.get(part);
        }

        if(!current.isFile){
            throw new IllegalArgumentException("Path is a directory");
        }

        return current.content.toString();

    }

    public void writeFile(String path, String content){

        // if file already exists, overwrite the content
        // if file doesn't exist, create a new file
        // if any intermediate directory doesn't exist, throw exception
        // if path ends with / or existing directory, throw exception

        if(path == null || path.isEmpty() || path.endsWith("/")) {
            throw new IllegalArgumentException("Invalid path");
        }
        Node current = root;

        String[] parts = path.split("/");
        for(int i = 1; i < parts.length - 1; i++){
            String part = parts[i];
            if(!current.children.containsKey(part) || current.children.get(part).isFile ){
                throw new IllegalArgumentException("Parent Directory doesn't exist");
            }
            current = current.children.get(part);
        }

        String fileName = parts[parts.length - 1];

        Node fileNode = current.children.getOrDefault(fileName, new Node(true));

        if(!fileNode.isFile){
            throw new IllegalArgumentException("File already exists with same name as directory " + fileName);
        }

        fileNode.content = new StringBuilder(content);
        current.children.put(fileName, fileNode);

    }

    public void ls(String path){
        // List all files and directories in the current directory
        // If path is a file, return the file name
        // If path is a directory, return the list of files and directories
        // If path doesn't exist, throw exception

        if(path == null || path.isEmpty() || path.endsWith("/")) {
            throw new IllegalArgumentException("Invalid path");
        }

        Node current = root;

        String parts[] = path.split("/");

        for(int i = 1; i < parts.length; i++){
            String part = parts[i];
            if(!current.children.containsKey(part)){
                throw new IllegalArgumentException("Path doesn't exist");
            }
            current = current.children.get(part);
        }

        List<String> result = new ArrayList<>();

        if(current.isFile){
            result.add(parts[parts.length - 1]);
        }else{
            for(Map.Entry<String, Node> entry: current.children.entrySet()){
                result.add(entry.getKey());
            }
            Collections.sort(result);
        }

        System.out.println(result);



    }

}

class Node{

    boolean isFile;
    StringBuilder content; // Only for files
    Map<String, Node> children; // Only for directories

    public Node(boolean isFile){
        this.isFile = isFile;
        if(isFile)
            this.content = new StringBuilder();
        else
            this.children = new HashMap<>();
    }
}
public class FileSystemDemo {
    public static void main(String[] args) {

        FileSystem fileSystem = new FileSystem();
        fileSystem.mkdir("/a");
        fileSystem.mkdir("/a/b");
        fileSystem.mkdir("/a/b/c");

        fileSystem.writeFile("/a/b/c/d.txt", "Hello World");
        fileSystem.writeFile("/a/b/c/e.txt", "Hello World");

        fileSystem.writeFile("/a/b/file1.txt", "Hello World 2");
        String output1 = fileSystem.readFile("/a/b/file1.txt");
        System.out.println(output1);
        fileSystem.readFile("/a/b/c/d.txt");

        fileSystem.ls("/a/b/c");

    }
}
