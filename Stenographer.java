import java.io.*;
import java.util.*;

public class Stenographer<E> {
  private List<E> data;
  private String title;

  public Stenographer(String title) {
    data = new ArrayList<E>();
    this.title = title;
  }

  public void record(E o) {
    data.add(o);
  }

  public void write() throws IOException {
    BufferedWriter out = writeFile(title);
    out.write(title+"\n");
    for (E o : data) {
      out.write(o+"\n");
    }
    out.close();
  }

  BufferedWriter writeFile(String filename) throws IOException {
    BufferedWriter out = new BufferedWriter(new FileWriter(filename));
    return out;
  }

}