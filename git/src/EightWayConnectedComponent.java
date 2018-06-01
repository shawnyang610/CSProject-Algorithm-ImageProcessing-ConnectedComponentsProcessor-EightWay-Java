//CS780-37 Project3 8-connected component algm
//Shawn Yang
import java.io.PrintWriter;
import java.util.Scanner;

public class EightWayConnectedComponent {
    Scanner infile;
    PrintWriter outfile1;
    PrintWriter outfile2;
    PrintWriter outfile3;
    public int numRows;
    public int numCols;
    public int minVal;
    public int maxVal;
    public int newMin;
    public int newMax;
    public int newLabel;
    public int componentCount;
    public int[][] zeroFramedAry;
    public int[] neighborAry;
    public int[] eqAry;
    ConnectedComponents[] componentProperties=null;
    class ConnectedComponents{
        public int label;
        public int numPixels;
        public int minRow;
        public int minCol;
        public int maxRow;
        public int maxCol;
        ConnectedComponents(int in_label){
            label=in_label;
            numPixels=0;
            minRow=-1;
            minCol=-1;
            maxRow=-1;
            maxCol=-1;
        }
    }
    public EightWayConnectedComponent(Scanner in_infile, PrintWriter in_outfile1,
                                      PrintWriter in_outfile2, PrintWriter in_outfile3){
        infile = in_infile;
        outfile1=in_outfile1;
        outfile2=in_outfile2;
        outfile3=in_outfile3;
        numRows=infile.nextInt();
        numCols=infile.nextInt();
        minVal=infile.nextInt();
        maxVal=infile.nextInt();
        newMin=0;
        newMax=0;
        newLabel=0;
        zeroFramedAry = new int[numRows+2][numCols+2];
        neighborAry= new int[5];
        componentCount=0;
        //initialising EQTable
        eqAry= new int[(numRows*numCols/2)];
        for (int i=0; i<eqAry.length;i++)
            eqAry[i]=i;
    }

    public void run(){
        //step1
        loadImage();
        zeroFramed();
        //step2 pass1
        eightCC_Pass1();
        outfile1.println("Pass1 PrettyPrint:");
        prettyPrint(outfile1);
        outfile1.println("EQAry:");
        printEQAry(outfile1);
        outfile1.println();
        //step3 pass2
        eightCC_Pass2();
        outfile1.println("Pass2 PrettyPrint:");
        prettyPrint(outfile1);
        outfile1.println("EQAry:");
        printEQAry(outfile1);
        outfile1.println();
        //step4 manageEQAry
        manageEQAry();
        outfile1.println("manageEQAry:");
        printEQAry(outfile1);
        outfile1.println();
        //step5 pass3
        eightCC_Pass3();
        outfile1.println("Pass3 PrettyPrint:");
        prettyPrint(outfile1);
        outfile1.println("EQAry:");
        printEQAry(outfile1);
        printNewImage(outfile2);
        printCCProperty(outfile3);
    }
    //mirror edge pixels to frame, top bottom first sides after
    private void zeroFramed (){
        for (int i=1; i<numCols+1;i++){
            zeroFramedAry[0][i]=0;
            zeroFramedAry[numRows+1][i]=0;
        }
        for (int i=0; i<numRows+2; i++){
            zeroFramedAry[i][0]=0;
            zeroFramedAry[i][numCols+1]=0;
        }
    }

    private void loadImage(){
        for (int r=0; r<numRows;r++) {
            for (int c = 0; c < numCols; c++)
                zeroFramedAry[r + 1][c + 1] = infile.nextInt();
        }
    }

    private void loadNeighbors(int r, int c, int direction){
        if (direction==0) { //direction==0:L to R, Top to Bottom.
                            //dirrecton!=0:R to L, Bottom to Top.
            neighborAry[0] = zeroFramedAry[r - 1][c - 1];
            neighborAry[1] = zeroFramedAry[r - 1][c];
            neighborAry[2] = zeroFramedAry[r - 1][c + 1];
            neighborAry[3] = zeroFramedAry[r][c - 1];
            neighborAry[4] = zeroFramedAry[r][c];
        }
        else {
            neighborAry[0] = zeroFramedAry[r][c];
            neighborAry[1] = zeroFramedAry[r][c+1];
            neighborAry[2] = zeroFramedAry[r+1][c-1];
            neighborAry[3] = zeroFramedAry[r+1][c];
            neighborAry[4] = zeroFramedAry[r+1][c+1];
        }
    }
    private void eightCC_Pass1(){
        //double for-loop goes thru whole image
        for (int r=1; r<numRows+1;r++){
            for (int c=1; c<numCols+1; c++){
                //load the corresponding 4 neighbors and pixel itself to neighborAry;
                loadNeighbors(r,c,0);
                //step2 if pixel >0
                if (neighborAry[4]>0){
                    //case 1 if a=b=c=d=0, p(i,j)=++newlabel
                    if (neighborAry[0]==neighborAry[1]&& neighborAry[1]==neighborAry[2]
                            &&neighborAry[2]==neighborAry[3]&& neighborAry[3]==0){
                        newLabel++;
                        zeroFramedAry[r][c]= newLabel;
                    }
                    //case 2,3
                    else {
                        if (isCase2_Or_Case3(1)) {
                            //case 2 if some of a,b,c,d have labels but are the same
                            zeroFramedAry[r][c]=smallestNonZeroLabel(1);
                        }
                        else{
                            //case 3
                            zeroFramedAry[r][c]=smallestNonZeroLabel(1);
                            updateEQAry(1,zeroFramedAry[r][c]);
                        }
                    }
                }
            }
        }
    }
    private void eightCC_Pass2(){
        for (int r=numRows; r>0; r--){
            for (int c=numCols; c>0; c--) {
                //load the corresponding 4 neighbors and pixel itself to neighborAry;
                loadNeighbors(r, c, 1);
                if (neighborAry[0] > 0) {
                    //case 1 if a=b=c=d=0, p(i,j)=++newlabel
                    if (neighborAry[3] == neighborAry[4] && neighborAry[1] == neighborAry[2]
                            && neighborAry[2] == neighborAry[3] && neighborAry[3] == 0) {
                        //do nothing
                    }
                    else {
                        if (isCase2_Or_Case3(2)) {
                            //case 2 do nothing
                        }
                        else{
                            //case 3
                            zeroFramedAry[r][c]=smallestNonZeroLabel(2);
                            updateEQAry(2,zeroFramedAry[r][c]);
                        }
                    }
                }
            }
        }
    }
    private void eightCC_Pass3(){
        //initialising ComponentsProperty Array
        componentProperties = new ConnectedComponents[componentCount+1];
        for (int i=1;i<componentCount+1; i++){
            componentProperties[i]=new ConnectedComponents(i);
        }

        //scan image from L to R, Top to Bottom
        for (int r=1; r<numRows+1; r++){
            for (int c=1; c<numCols+1; c++){
                //labeling
                zeroFramedAry[r][c]=eqAry[zeroFramedAry[r][c]];
                //count pixels for each component
                if (zeroFramedAry[r][c]!=0) {
                    componentProperties[zeroFramedAry[r][c]].numPixels++;
                }
                //Bounding box
                if (zeroFramedAry[r][c]!=0) {
                    if (componentProperties[zeroFramedAry[r][c]].minRow < 0)
                        componentProperties[zeroFramedAry[r][c]].minRow = r;
                    else {
                        if (r < componentProperties[zeroFramedAry[r][c]].minRow) {
                            componentProperties[zeroFramedAry[r][c]].minRow = r;
                        }
                    }
                    if (componentProperties[zeroFramedAry[r][c]].minCol < 0)
                        componentProperties[zeroFramedAry[r][c]].minCol = c;
                    else {
                        if (c < componentProperties[zeroFramedAry[r][c]].minCol) {
                            componentProperties[zeroFramedAry[r][c]].minCol = c;
                        }
                    }
                    if (componentProperties[zeroFramedAry[r][c]].maxRow < 0)
                        componentProperties[zeroFramedAry[r][c]].maxRow = r;
                    else {
                        if (r > componentProperties[zeroFramedAry[r][c]].maxRow) {
                            componentProperties[zeroFramedAry[r][c]].maxRow = r;
                        }
                    }
                    if (componentProperties[zeroFramedAry[r][c]].maxCol < 0)
                        componentProperties[zeroFramedAry[r][c]].maxCol = c;
                    else {
                        if (c > componentProperties[zeroFramedAry[r][c]].maxCol) {
                            componentProperties[zeroFramedAry[r][c]].maxCol = c;
                        }
                    }
                }
                record_newMin_and_newMax(zeroFramedAry[r][c]);
            }
        }



    }
    private void updateEQAry(int selector, int smallestNum){
        if (selector==1) {
            for (int i = 0; i < 4; i++) {
                if (neighborAry[i] > smallestNum)
                    eqAry[neighborAry[i]] = smallestNum;
            }
        }
        else if ( selector ==2){
            for (int i = 0; i < 5; i++) {
                if (neighborAry[i] > smallestNum)
                    eqAry[neighborAry[i]] = smallestNum;
            }
        }
        else{System.exit(1);}
    }
    private void manageEQAry(){
        for (int i=1; i<=newLabel; i++) {
            if (eqAry[i] ==i){
                componentCount++;
                eqAry[i]=componentCount;
            }
            else {
                eqAry[i]=eqAry[eqAry[i]];
            }
        }
    }
    private void printCCProperty(PrintWriter in_outfile){
        in_outfile.println(numRows+" "+numCols+" "+newMin+" "+newMax);
        in_outfile.println(componentCount);
        for (int i=1; i<componentProperties.length;i++) {
            in_outfile.println(i);
            in_outfile.println(componentProperties[i].numPixels);
            in_outfile.println((--componentProperties[i].minRow)+" "+(--componentProperties[i].minCol));
            in_outfile.println((--componentProperties[i].maxRow)+" "+(--componentProperties[i].maxCol));
        }

    }
    private void prettyPrint(PrintWriter in_outfile){
        for (int r=0; r<numRows+2; r++){
            for (int c=0; c<numCols+2; c++){
                if (zeroFramedAry[r][c]==0)
                    in_outfile.print("  ");
                else
                    in_outfile.print(zeroFramedAry[r][c]+" ");
            }
            in_outfile.println();
        }
    }
    private void printEQAry(PrintWriter in_outfile){
        for (int i=1; i<newLabel; i++){
            in_outfile.print("Index:"+i+":"+eqAry[i]+", ");
        }
        in_outfile.println();
    }
    private void printNewImage(PrintWriter in_outfile){
        in_outfile.println(numRows+" "+numCols+" "+newMin+" "+newMax);
        for (int r=1; r<numRows+1;r++){
            for (int c=1; c<numCols+1;c++){
                    in_outfile.print(zeroFramedAry[r][c]+" ");
            }
            in_outfile.println();
        }
    }
    //this method can only be used after picking out case1
    private boolean isCase2_Or_Case3(int selector){//selector==1:for first pass, selector==2:for 2nd pass
        int previousIndex=99999;
        if (selector==1){
            for (int i=0; i<4; i++){
                if (neighborAry[i]>0 && previousIndex ==99999){
                    previousIndex = i;
                } else if (neighborAry[i]>0){
                    if (neighborAry[i]!=neighborAry[previousIndex])
                        return false;
                }
            }
        }
        else if(selector==2){
            for (int i=1; i<5; i++){
                if (neighborAry[i]>0) {
                    if(neighborAry[i]!=neighborAry[0])
                        return false;
                }
            }
        }
        else {
            System.exit(1);
        }
        return true;
    }

    private int smallestNonZeroLabel (int selector){ //selector==1:for first pass, selector==2:for 2nd pass
        int smallest=99999;
        if (selector ==1){
            for (int i=0; i<4; i++){
                if (neighborAry[i]>0&& neighborAry[i]<smallest)
                    smallest=neighborAry[i];
            }
        } else if (selector ==2){
            for (int i=0; i<5; i++){
                if (neighborAry[i]>0 && neighborAry[i]<smallest)
                    smallest=neighborAry[i];
            }
        } else{System.exit(1);}
        return smallest;
    }

    private void record_newMin_and_newMax(int in_num){
        //reset these numbers for each pass
        if (in_num<newMin)
            newMin=in_num;
        if (in_num>newMax)
            newMax=in_num;
    }
}
