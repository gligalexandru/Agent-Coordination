import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomRole implements Wolf{
    public boolean isLeader = false;
    public final static BlackBoard blackBoard = new BlackBoard();

    private void findAlpha() {
        if(!BlackBoard.isAssigned){
            this.isLeader = true;
            BlackBoard.isAssigned = true;
        }
    }

    private int[] searchPrey(List<int[]> preysSight) {
        if(preysSight.isEmpty()){
            Random r = new Random();
            return new int[]{r.nextInt(3)-1, r.nextInt(3)-1};
        }else{
            int[] prey_dist = new int[preysSight.size()];
            for(int i= 0; i<preysSight.size(); i++){
                prey_dist[i] = Math.abs(preysSight.get(i)[0]) + Math.abs(preysSight.get(i)[1]);
            }

            int min_index = -1;
            int min_distance = 100000;
            for(int i=0; i<prey_dist.length; i++){
                if (prey_dist[i] < min_distance){
                    min_distance = prey_dist[i];
                    min_index = i;
                }
            }
            int[] next_move = new int[2];

            int[] prey = preysSight.get(min_index);
            next_move[0] = (prey[0] > 0) ? -1 : (prey[0] < 0) ? 1 : 0;
            next_move[1] = (prey[1] > 0) ? -1 : (prey[1] < 0) ? 1 : 0;

            return next_move;
        }
    }

    @Override
    public int[] moveAll(List<int[]> wolvesSight, List<int[]> preysSight) {
        findAlpha();
        if(this.isLeader){
            List<int[]> relDistWolves = new ArrayList<>();
            for(int i=0; i<wolvesSight.size(); i++){
                int[] wolfRel = wolvesSight.get(i);
                if(!(wolfRel[0]==0 && wolfRel[1]==0)){
                    relDistWolves.add(wolfRel);
                }
            }
            blackBoard.setRelDistWolves(relDistWolves);
            return searchPrey(preysSight);
        }else {
            int[] nextMove = new int[2];

            List<int[]> relDistWolves = blackBoard.getRelDistWolves();

            for (int i = 0; i < wolvesSight.size(); i++) {
                for (int j = 0; j < relDistWolves.size(); j++) {
                    int[] relDistWolf = relDistWolves.get(j);
                    int[] wolfSight = wolvesSight.get(i);

                    if (wolfSight[0] == -relDistWolf[0] && wolfSight[1] == -relDistWolf[1]) {
                        for (int k = 0; k < nextMove.length; k++) {
                            if (wolfSight[k] > 0) {
                                nextMove[k] = -1;
                            } else if (wolfSight[k] < 0) {
                                nextMove[k] = 1;
                            } else {
                                nextMove[k] = 0;
                            }
                        }

                    }
                }
            }
            return nextMove;
        }
    }

    private int determineLim(int[] nextMove){
        // Check if there is no diagonal
        boolean hasDiagonal = true;
        // save index where there is value 0
        int zeroValueIndex = -1;
        for(int i=0; i<nextMove.length; i++){
            int move = nextMove[i];
            // if the move does not move in to some specific row/column
            // save the index and say that there is no diagonal movement
            if(move==0){
                hasDiagonal = false;
                zeroValueIndex = i;
            }
        }
        int nonZeroValueIndex = -1;
        // make a random choice in which direction to move
        if(hasDiagonal){
            Random r = new Random();
            nonZeroValueIndex = r.nextInt(2);
            // if there is no diagonal movement, get the index of a non zero value
        }else {
            nonZeroValueIndex = zeroValueIndex == 0 ? 1 : 0;
        }
        // returns 0 for No Movement, 1 for North, 2 for East, 3 for South, 4 for West
        // if there is no movement
        if(nextMove[nonZeroValueIndex]==0){
            return 0;
        }else{
            // column movement
            if(nonZeroValueIndex==1){
                return 3 - nextMove[nonZeroValueIndex];
                // row movement
            }else{
                return 2 + nextMove[nonZeroValueIndex];
            }
        }
    }

    @Override
    public int moveLim(List<int[]> wolvesSight, List<int[]> preysSight) {
        int nextMove = this.determineLim(this.moveAll(wolvesSight, preysSight));
        return nextMove;
    }
}