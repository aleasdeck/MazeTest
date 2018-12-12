package packet;

import java.util.Random;

public class Main {
    public static void main(String[] args) {

        System.out.println("Hello world! Сегодня мы будем строить лабиринт, \nну шо народ, погнале нах....й, е...ан..й рооот");
        System.out.println("_______________________________________________\n");

        int[][] maze = generateMaze(5, true);
        for(int i = 0; i < maze.length; i++) {
            for (int j = 0; j < maze.length; j++) {
                System.out.print(maze[i][j] + " ");
            }
            System.out.println();
        }
    }

    private static int[][] generateMaze(int size, boolean multiPath){

        Random random = new Random();
        int maze[][] = new int[size][size];                                                                             // Пустой квадрат
        maze[0][0] = 1;                                                                                                 // Задаем стартовую позицию как посещенную
        int keyCellsCount = (size % 2 != 0) ? ((int)Math.pow((size + 1), 2) / 4) : ((int)Math.pow((size), 2) / 4);      // Колличество ключевых ячеек, где будем останавливаться
        int filledKeyCells = 1;                                                                                         // Заполненные ключевые ячейки
        int nowPos = 0;                                                                                                 // Текущая позиция
        Coordinates[] position = new Coordinates[keyCellsCount];                                                        // Массив с пройденными координатами
        position[nowPos] = new Coordinates();
        position[nowPos].setX(0);
        position[nowPos].setY(0);

        while(filledKeyCells < keyCellsCount){
            if(checkCellsAround(maze, position[nowPos])){
                int rndValue = random.nextInt(4) + 1;
                switch(rndValue){
                    case 1: { // x- вверх
                        if(!checkCellGoTo(maze, position[nowPos], rndValue, 2)) break;
                        maze[position[nowPos].getX() - 1][position[nowPos].getY()] = 1;
                        maze[position[nowPos].getX() - 2][position[nowPos].getY()] = 1;
                        nowPos++;
                        position[nowPos] = new Coordinates();
                        position[nowPos].setX(position[nowPos - 1].getX() - 2);
                        position[nowPos].setY(position[nowPos - 1].getY());
                        filledKeyCells++;
                        break;
                    }
                    case 2: { // x+ вниз
                        if(!checkCellGoTo(maze, position[nowPos], rndValue, 2)) break;
                        maze[position[nowPos].getX() + 1][position[nowPos].getY()] = 1;
                        maze[position[nowPos].getX() + 2][position[nowPos].getY()] = 1;
                        nowPos++;
                        position[nowPos] = new Coordinates();
                        position[nowPos].setX(position[nowPos - 1].getX() + 2);
                        position[nowPos].setY(position[nowPos - 1].getY());
                        filledKeyCells++;
                        break;
                    }
                    case 3: { // y- влево
                        if(!checkCellGoTo(maze, position[nowPos], rndValue, 2)) break;
                        maze[position[nowPos].getX()][position[nowPos].getY() - 1] = 1;
                        maze[position[nowPos].getX()][position[nowPos].getY() - 2] = 1;
                        nowPos++;
                        position[nowPos] = new Coordinates();
                        position[nowPos].setX(position[nowPos - 1].getX());
                        position[nowPos].setY(position[nowPos - 1].getY() - 2);
                        filledKeyCells++;
                        break;
                    }
                    case 4: { // y+ вправо
                        if(!checkCellGoTo(maze, position[nowPos], rndValue, 2)) break;
                        maze[position[nowPos].getX()][position[nowPos].getY() + 1] = 1;
                        maze[position[nowPos].getX()][position[nowPos].getY() + 2] = 1;
                        nowPos++;
                        position[nowPos] = new Coordinates();
                        position[nowPos].setX(position[nowPos - 1].getX());
                        position[nowPos].setY(position[nowPos - 1].getY() + 2);
                        filledKeyCells++;
                        break;
                    }
                }
            }
            else{
                nowPos--;
            }
        }

        if(multiPath){                                                      // Ломаем стены для вариативности прохождения

            int breakingWallsCount = (int)Math.pow((size / 5), 2);          // Колличество ломаемых стен

            for(int i = 0; i <  breakingWallsCount; i++) {
                boolean exit = false;
                do {
                    int randomX = random.nextInt(size - 1);
                    int randomY = random.nextInt(size - 1);
                    int way = random.nextInt(4) - 1;
                    if((randomX % 2 == 0 && randomY % 2 != 0) || (randomX % 2 != 0 && randomY % 2 == 0)) {
                        if(checkCellGoTo(maze, new Coordinates(randomX, randomY), way, 1)) {
                            System.out.println("X:" + randomX + " Y:" + randomY);
                            maze[randomX][randomY] = 1;
                            exit = true;
                        }
                    }
                } while(!exit);
            }

            /*int breakingWallsCount = (int)Math.pow((size / 5), 2);          // Колличество ломаемых стен
            int posIndexForBreak, wayForBreak;

            for(int i = 0; i <  breakingWallsCount; i++){
                boolean exit = false;
                do {
                    posIndexForBreak = random.nextInt(position.length);
                    wayForBreak = random.nextInt(4) + 1;

                    System.out.print("Pos:" + posIndexForBreak + " ");
                    for(Coordinates coords : position) System.out.print("|x:" + coords.getX() + "y:" + coords.getY());
                    System.out.print(" X:" + position[posIndexForBreak].getX());
                    System.out.print(" Y:" + position[posIndexForBreak].getY());
                    System.out.println(" Way:" + wayForBreak);

                    if(checkCellGoTo(maze, position[posIndexForBreak], wayForBreak, 1)) {
                        if(wayForBreak == 1) {
                            System.out.println("1");
                            maze[position[posIndexForBreak].getX() - 1][position[posIndexForBreak].getY()] = 1;
                            exit = true;
                            } // вверх
                        if(wayForBreak == 2) {
                            System.out.println("2");
                            maze[position[posIndexForBreak].getX() + 1][position[posIndexForBreak].getY()] = 1;
                            exit = true;
                            } // вниз
                        if(wayForBreak == 3) {
                            System.out.println("3");
                            maze[position[posIndexForBreak].getX()][position[posIndexForBreak].getY() - 1] = 1;
                            exit = true;
                            } // влево
                        if(wayForBreak == 4) {
                            System.out.println("4");
                            maze[position[posIndexForBreak].getX()][position[posIndexForBreak].getY() + 1] = 1;
                            exit = true;
                            } // вправо
                    }
                }
                while(!exit);
            }*/
        }

        return maze;
    }

    private static boolean checkCellsAround(int[][] maze, Coordinates position) {

        if(position.getX() - 2 >= 0) if(maze[position.getX() - 2][position.getY()] == 0) return true;            // x-
        if(position.getX() + 2 <= maze.length) if(maze[position.getX() + 2][position.getY()] == 0) return true;  // x+
        if(position.getY() - 2 >= 0) if(maze[position.getX()][position.getY() - 2] == 0) return true;            // y-
        if(position.getY() + 2 <= maze.length) if(maze[position.getX()][position.getY() + 2] == 0) return true;  // y+

        return false;
    }

    private static boolean checkCellGoTo(int[][] maze, Coordinates position, int way, int keyOrWall) {  // way - направление, keyOrWall - 2-ключевая клетка, 1-стена

        if(way == 1) if(position.getX() - 2 >= 0) if(maze[position.getX() - keyOrWall][position.getY()] == 0) return true;            // x-
        if(way == 2) if(position.getX() + 2 <= maze.length) if(maze[position.getX() + keyOrWall][position.getY()] == 0) return true;  // x+
        if(way == 3) if(position.getY() - 2 >= 0) if(maze[position.getX()][position.getY() - keyOrWall] == 0) return true;            // y-
        if(way == 4) if(position.getY() + 2 <= maze.length) if(maze[position.getX()][position.getY() + keyOrWall] == 0) return true;  // y+

        return false;
    }

    private static class Coordinates {

        private int x;
        private int y;

        public int getX() {
            return x;
        }

        public Coordinates() {

        }

        public Coordinates(int x, int y){
            this.x = x;
            this.y = y;
        }

        public int getY() {
            return y;
        }

        public void setX(int x) {
            this.x = x;
        }

        public void setY(int y) {
            this.y = y;
        }
    }
}
