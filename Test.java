import java.io.*;
import javax.sound.midi.*;
import java.util.Random;

class Test {
    //遺伝子の数
    static int pop_size = 60;
    //選択する
    //static float select_number = pop_size/10;
    //遺伝子の長さ
    static int gene_length = 16;
    //世代数
    static int generation = 60;
    //交叉する確率
    static int cross_rate = 8;
    //遺伝子を生成
    static int gene[][][] = new int[pop_size][gene_length][2];
    //正しい遺伝子初期化
    static int correct_gene[] = new int[gene_length];
    //次世代用のコピー
    static int copy_gene[][][] = new int[pop_size][gene_length][2];
    //エリート保存する割合
    static int elite_par = pop_size/10;
     //遺伝子の一致の点数つけるための配列
    static int macth_point[] = new int[pop_size];
    //音の長さ
    static int music_time = 500;

    public static void main(String[] args) throws Exception {
        Receiver receiver = MidiSystem.getReceiver();
        ShortMessage message = new ShortMessage();
        //エリート保存する数を偶数にする
        if(elite_par%2 == 1){
            elite_par +=1;
        }
        //初期集団生成
        createCell();
        //正しい遺伝子情報を入れる
        // for(int i=0;i<gene_length;i++){
        //     correct_gene[i] = 7;
        // }
        //カエルの歌
        correct_gene[0] = 0;
        correct_gene[1] = 1;
        correct_gene[2] = 2;
        correct_gene[3] = 3;
        correct_gene[4] = 2;
        correct_gene[5] = 1;
        correct_gene[6] = 0;
        correct_gene[7] = 0;
        correct_gene[8] = 2;
        correct_gene[9] = 3;
        correct_gene[10] = 4;
        correct_gene[11] = 5;
        correct_gene[12] = 4;
        correct_gene[13] = 3;
        correct_gene[14] = 2;
        correct_gene[15] = 2;
        //チャルメラ
        // correct_gene[0] = 5;
        // correct_gene[1] = 6;
        // correct_gene[2] = 7;
        // correct_gene[3] = 7;
        // correct_gene[4] = 7;
        // correct_gene[5] = 7;
        // correct_gene[6] = 6;
        // correct_gene[7] = 5;
        // correct_gene[8] = 5;
        // correct_gene[9] = 6;
        // correct_gene[10] = 7;
        // correct_gene[11] = 5;
        // correct_gene[12] = 6;
        // correct_gene[13] = 5;
        // correct_gene[14] = 5;
        // correct_gene[15] = 5;

        //交叉させる遺伝子の番号を格納する配列
        int cross_num[] = new int[2];
        // GA開始ジェネレーションの数だけ
        correct_gene_sound();
        System.out.println("Enter キーを押してプログラムを終了します>");
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        reader.readLine();
        // Thread.sleep(10000);
        for(int i=0;i<generation;i++){
            if(i == 1 || i==generation/2){
                gene_sound();
                // Thread.sleep(5000);
                System.out.println("Enter キーを押してプログラムを終了します>");
                reader = new BufferedReader(new InputStreamReader(System.in));
                reader.readLine();
            }
            //点数をつける
            calcMatch();
            System.out.println(i+"世代め");
            for(int j=0;j<pop_size;j++){
                if(j==0){
                System.out.println(j+"遺伝子"+macth_point[j]+"点");
                }
            }
            //コピーを初期か
            copy_gene = new int[pop_size][gene_length][2];
            //エリート保存ん
            keepElite();
            //ルーレット選択
            rouletteSelection();
            //交叉
            singlePointCrossOver();
            //突然変異
            mutation_binary();
            // for(int k=0;k<pop_size;k++){
            //     System.out.print(k+"コピー遺伝子");
            //     for(int j=0;j<gene_length;j++){
            //         System.out.print(copy_gene[k][j][0]);      
            //     }
            //     System.out.println("");  
            // }
            //交叉したやつを移す
            for(int j=0;j<pop_size;j++){
                for(int k=0;k<gene_length;k++){
                    for(int l=0;l<2;l++){
                    gene[j][k][l] = copy_gene[j][k][l];
                    }
                }
            }
        }
        gene_sound();
        calcMatch();
    }

    /*
    int onp 音の高さ
    int time 流す秒数
    */
    public static void gene_sound() throws Exception {
        Receiver receiver = MidiSystem.getReceiver();
        ShortMessage message = new ShortMessage();
        int onp[] = new int[8];
        for(int i=0;i<onp.length;i++){
            onp[i] = 60+i*2;
        }
        int Duplicate = 0;
        for(int i=0;i<gene_length;i++){
            if(i < gene_length-1){
                if(gene[0][i+1][0]==gene[0][i][0]){
                    Duplicate++;
                    continue;
                }
            }
            sound(onp[gene[0][i][0]],music_time+Duplicate*music_time);
            Duplicate=0;
        }
    }
    public static void correct_gene_sound() throws Exception {
        Receiver receiver = MidiSystem.getReceiver();
        ShortMessage message = new ShortMessage();
        int onp[] = new int[8];
        for(int i=0;i<onp.length;i++){
            onp[i] = 60+i*2;
        }
        int Duplicate = 0;
        for(int i=0;i<gene_length;i++){
            if(i < gene_length-1){
                if(correct_gene[i+1]==correct_gene[i]){
                    Duplicate++;
                    continue;
                }
            }
            sound(onp[correct_gene[i]],music_time+Duplicate*music_time);
            Duplicate=0;
        }
    }
    public static void sound(int onp,int time) throws Exception {
        Receiver receiver = MidiSystem.getReceiver();
        ShortMessage message = new ShortMessage();
        //音を鳴らす
        message.setMessage(ShortMessage.NOTE_ON, onp, 127);
        receiver.send(message, -1);
        //余韻を流す
        Thread.sleep(time);
        //余韻を止める
        message.setMessage(ShortMessage.NOTE_OFF, onp, 127);
        receiver.send(message, -1);
    }

    public static void createCell(){
        //Randomクラスのインスタンス化
        Random rnd = new Random();
        //遺伝子を入れるために，個体数ｘ遺伝子長
        for(int i=0;i<pop_size;i++){
            for(int j=0;j<gene_length;j++){
                for(int k=0;k<2;k++){
                    int rnd_cell = rnd.nextInt(8);
                    //System.out.println(rnd_cell);
                    //ランダムで数字作って入れる
                    gene[i][j][0] = rnd_cell;

                    int rnd_time = rnd.nextInt(2);
                    gene[i][j][1] = 125 + rnd_time*125;
                } 
            }     
        }
    }
    public static void calcMatch(){
        //点数を初期化
        for(int i=0;i<pop_size;i++){
            macth_point[i]=0;
        }
        //遺伝子１個１個を比較　個体数ｘ遺伝子長
        for(int i=0;i<pop_size;i++){
            for(int j=0;j<gene_length;j++){
                // for(int k=0;k<2;k++){
                    if(gene[i][j][0] == correct_gene[j]){
                        macth_point[i]++;
                    }
                // }
            }     
        }
        //合計点を出す
        int sum_macth_point=0;
        for(int i=0;i<pop_size;i++){
            sum_macth_point += macth_point[i];
        } 
        System.out.println("点数"+sum_macth_point);
    }
    public static void keepElite(){
        //作業用の配列と変数
        int sorted_index[]= new int[pop_size];
        int buff_i;
        for(int i=0; i<pop_size; i++){
          sorted_index[i] = 0;
        }
        sorted_index[0] = 0;
        //今並んでる番号を入れる
        for(int i=0; i<pop_size; i++){
          sorted_index[i] = i;
        }
        //番号をバブルソート
        for(int i=0; i<pop_size; i++){
          for(int j=(i+1); j<pop_size; j++){
            if( macth_point[sorted_index[i]] < macth_point[sorted_index[j]] ){
              buff_i = sorted_index[i];
              sorted_index[i] = sorted_index[j];
              sorted_index[j] = buff_i;
            }
          }
        }
        //エリートが配列の上に来てるのでそれをコピーの上位に配置
        for(int i=0;i<elite_par;i++){
            for(int j=0;j<gene_length;j++){
                copy_gene[i][j][0] = gene[sorted_index[i]][j][0];
            }
        }
    }
    public static void rouletteSelection(){
        //Randomクラスのインスタンス化
        Random rnd = new Random();
        //適応度の合計
        int sum_macth_point = 0;
        //適応度を全部足す
        for(int i=0;i<pop_size;i++){
            sum_macth_point += macth_point[i];
        }
        for(int i=elite_par;i<pop_size;i++){
            //合計の中から一つの数をとる
            int select = rnd.nextInt(sum_macth_point) + 1;
            int sum_select = 0;
            //遺伝子の選択
            for(int j=0;j<pop_size;j++){
                sum_select += macth_point[j];
                //合計点を超えたらその時の番号の配列をコピーに入れていく
                if(select <= sum_select){
                    for(int k=0;k<gene_length;k++){
                    copy_gene[i][k][0]= gene[j][k][0];
                    }
                    break;
                }

            }
        }
    }
    public static void singlePointCrossOver(){
        //Randomクラスのインスタンス化
        Random rnd = new Random();
        //エリートを除いて交叉させる
        for(int i=elite_par;i<pop_size;i+=2){
            //交叉する確率cross_rate
            if(cross_rate>(rnd.nextInt(10)+1)){
                //交叉するポイントをランダムで決める
                int cross_point = rnd.nextInt(gene_length);
                int work_gene[][] =new int[gene_length][2];
                for(int j=0;j<cross_point;j++){
                    for(int k=0;k<2;k++){
                        //交叉
                        work_gene[j][k] = copy_gene[i][j][k];
                        copy_gene[i][j][k] = copy_gene[i+1][j][k];
                        copy_gene[i+1][j][k] = work_gene[j][k];
                    }
                }
            }
        }
    }
    public static void mutation_binary(){
        //Randomクラスのインスタンス化
        Random rnd = new Random();
        int pop_select;
        int gene_select;
        int change_onp;
        //ランダムで個体数分突然変異
        for(int i=0;i<pop_size/3;i++){
            pop_select = rnd.nextInt(pop_size-elite_par)+elite_par;
            gene_select = rnd.nextInt(gene_length);
            change_onp = rnd.nextInt(8);
            copy_gene[pop_select][gene_select][0] = change_onp; 
        }
    }
}