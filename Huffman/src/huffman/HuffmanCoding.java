package huffman;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * This class contains methods which, when used together, perform the
 * entire Huffman Coding encoding and decoding process
 * 
 * @author Ishaan Ivaturi
 * @author Prince Rawal
 */
public class HuffmanCoding {
    private String fileName;
    private ArrayList<CharFreq> sortedCharFreqList;
    private TreeNode huffmanRoot;
    private String[] encodings;

    /**
     * Constructor used by the driver, sets filename
     * DO NOT EDIT
     * @param f The file we want to encode
     */
    public HuffmanCoding(String f) { 
        fileName = f; 
    }

    /**
     * Reads from filename character by character, and sets sortedCharFreqList
     * to a new ArrayList of CharFreq objects with frequency > 0, sorted by frequency
     */
    public void makeSortedList() {
        StdIn.setFile(fileName);
        ArrayList<CharFreq> res=new ArrayList<>();
        ArrayList<Character> l=new ArrayList<>();
        HashMap<Character,Double> ha=new HashMap<>();
        int count=0;
        while(StdIn.hasNextChar()){
            Character k=StdIn.readChar();
            double add=ha.getOrDefault(k,0.0)+1;
            ha.putIfAbsent(k, add);
            ha.replace(k,add);
            count++;
            if (!l.contains(k)) l.add(k);
        }
        for (int i=0;i<l.size();i++){
            Character b=l.get(i);
            double pos=ha.get(b)/count;
            CharFreq in=new CharFreq();
            in.setCharacter(b);
            in.setProbOcc(pos);
            res.add(in);
        }
        if (res.size()==1){
            CharFreq r=res.get(0);
            int xx=0;
            if ((int)r.getCharacter()!=127) xx=(int)r.getCharacter()+1;
            CharFreq rr=new CharFreq((char)xx,0.00);
            res.add(rr);
        }
        Collections.sort(res);
        sortedCharFreqList=res;
    }

    /**
     * Uses sortedCharFreqList to build a huffman coding tree, and stores its root
     * in huffmanRoot
     */
    public void makeTree() {
    Queue<TreeNode> source=new Queue<>();
    Queue<TreeNode> target=new Queue<>();
    for (int i=0;i<sortedCharFreqList.size();i++){
            CharFreq ch=sortedCharFreqList.get(i);
            TreeNode T=new TreeNode(ch,null,null);
            source.enqueue(T);
    }
    huffmanRoot=make(source,target);
    }
    private TreeNode make(Queue<TreeNode> a,Queue<TreeNode> b){
            TreeNode a1=new TreeNode();
            TreeNode a2=new TreeNode();
        while(!a.isEmpty()||b.size()!=1){
                if (b.isEmpty()){
                    a1=a.dequeue();
                    a2=a.dequeue();
                }
                else if(a.isEmpty()){
                    a1=b.dequeue();
                    a2=b.dequeue();
                }
                else if (!b.isEmpty()){
                    if (a.peek().getData().getProbOcc()<=b.peek().getData().getProbOcc()) a1=a.dequeue();
                    else  a1=b.dequeue();
                    if (a.isEmpty()){
                        a2=b.dequeue();
                    }
                    else if (b.isEmpty()){
                        a2=a.dequeue();
                    }
                    else {
                        if (a.peek().getData().getProbOcc()<=b.peek().getData().getProbOcc()) a2=a.dequeue();
                        else a2=b.dequeue();
                    }
                }   
                CharFreq add=new CharFreq(null,a1.getData().getProbOcc()+a2.getData().getProbOcc());
                TreeNode adder=new TreeNode(add,a1,a2);
                b.enqueue(adder);
            }
            return b.dequeue();
    }
    /**
     * Uses huffmanRoot to create a string array of size 128, where each
     * index in the array contains that ASCII character's bitstring encoding. Characters not
     * present in the huffman coding tree should have their spots in the array left null.
     * Set encodings to this array.
     */
    public void makeEncodings() {
    String[] res=new String[128];
    res=addt();
    encodings=res;
    }
    private String[] addt(){
        String[] res=new String[128];
        ArrayList<String> b=new ArrayList<>();
        ArrayList<Character> c=new ArrayList<>();
        find(b,c,huffmanRoot,"");
        for (int i=0;i<b.size();i++){
            res[(int)c.get(i)]=b.get(i);
        }
        return res;
    }
    private void find(ArrayList<String> ch,ArrayList<Character> h,TreeNode t,String res){
        if (t.getData().getCharacter()!=null){
               ch.add(res);
               h.add(t.getData().getCharacter());
        }
    if (t.getLeft()!=null) find(ch,h,t.getLeft(),res+"0");
    if (t.getRight()!=null) find(ch,h,t.getRight(),res+"1");
    }
    /**
     * Using encodings and filename, this method makes use of the writeBitString method
     * to write the final encoding of 1's and 0's to the encoded file.
     * 
     * @param encodedFile The file name into which the text file is to be encoded
     */
    public void encode(String encodedFile) {
        StdIn.setFile(fileName);
        StringBuilder res=new StringBuilder();
        while(StdIn.hasNextChar()){
            Character k=StdIn.readChar();
            res.append(encodings[(int)k]);
        }
        writeBitString(encodedFile,res.toString());
    }
    
    /**
     * Writes a given string of 1's and 0's to the given file byte by byte
     * and NOT as characters of 1 and 0 which take up 8 bits each
     * DO NOT EDIT
     * 
     * @param filename The file to write to (doesn't need to exist yet)
     * @param bitString The string of 1's and 0's to write to the file in bits
     */
    public static void writeBitString(String filename, String bitString) {
        byte[] bytes = new byte[bitString.length() / 8 + 1];
        int bytesIndex = 0, byteIndex = 0, currentByte = 0;

        // Pad the string with initial zeroes and then a one in order to bring
        // its length to a multiple of 8. When reading, the 1 signifies the
        // end of padding.
        int padding = 8 - (bitString.length() % 8);
        String pad = "";
        for (int i = 0; i < padding-1; i++) pad = pad + "0";
        pad = pad + "1";
        bitString = pad + bitString;

        // For every bit, add it to the right spot in the corresponding byte,
        // and store bytes in the array when finished
        for (char c : bitString.toCharArray()) {
            if (c != '1' && c != '0') {
                System.out.println("Invalid characters in bitstring");
                return;
            }

            if (c == '1') currentByte += 1 << (7-byteIndex);
            byteIndex++;
            
            if (byteIndex == 8) {
                bytes[bytesIndex] = (byte) currentByte;
                bytesIndex++;
                currentByte = 0;
                byteIndex = 0;
            }
        }
        
        // Write the array of bytes to the provided file
        try {
            FileOutputStream out = new FileOutputStream(filename);
            out.write(bytes);
            out.close();
        }
        catch(Exception e) {
            System.err.println("Error when writing to file!");
        }
    }

    /**
     * Using a given encoded file name, this method makes use of the readBitString method 
     * to convert the file into a bit string, then decodes the bit string using the 
     * tree, and writes it to a decoded file. 
     * 
     * @param encodedFile The file which has already been encoded by encode()
     * @param decodedFile The name of the new file we want to decode into
     */
    public void decode(String encodedFile, String decodedFile) {
        StdOut.setFile(decodedFile);
        String k=readBitString(encodedFile);
        int i=0;
        while(i<k.length()){
            for (int j=0;j<128;j++){
                if (encodings[j]!=null){
                String com1=encodings[j];
                int ad=com1.length();
                if (ad+i<=k.length()){
                    String com2=k.substring(i,i+ad);
                if (com1.compareTo(com2)==0){
                    StdOut.print((char)j);
                    i=i+ad;
                    break;
                }
                }
                }
            }
        }
    }

    /**
     * Reads a given file byte by byte, and returns a string of 1's and 0's
     * representing the bits in the file
     * DO NOT EDIT
     * 
     * @param filename The encoded file to read from
     * @return String of 1's and 0's representing the bits in the file
     */
    public static String readBitString(String filename) {
        String bitString = "";
        
        try {
            FileInputStream in = new FileInputStream(filename);
            File file = new File(filename);

            byte bytes[] = new byte[(int) file.length()];
            in.read(bytes);
            in.close();
            
            // For each byte read, convert it to a binary string of length 8 and add it
            // to the bit string
            for (byte b : bytes) {
                bitString = bitString + 
                String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0');
            }

            // Detect the first 1 signifying the end of padding, then remove the first few
            // characters, including the 1
            for (int i = 0; i < 8; i++) {
                if (bitString.charAt(i) == '1') return bitString.substring(i+1);
            }
            
            return bitString.substring(8);
        }
        catch(Exception e) {
            System.out.println("Error while reading file!");
            return "";
        }
    }

    /*
     * Getters used by the driver. 
     * DO NOT EDIT or REMOVE
     */

    public String getFileName() { 
        return fileName; 
    }

    public ArrayList<CharFreq> getSortedCharFreqList() { 
        return sortedCharFreqList; 
    }

    public TreeNode getHuffmanRoot() { 
        return huffmanRoot; 
    }

    public String[] getEncodings() { 
        return encodings; 
    }
}
