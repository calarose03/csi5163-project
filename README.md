Project: Implementation of MIS Algorithms
----------------------------------------

The program takes a file and a number, builds a graph and applies one of the algorithms to find its maximum independent set.

To run the Elections, run this command to compile:

`javac -cp . implementation/RunMIS.java`

Then, run this command:

`java election.RunElection [filename] [number]`

where `filename` refers to the file of the graph you wish to find a MIS for. These files can be found in the `data` folder.

The `number` option will act as follows:

- `1`: Run the approximation algorithm on the file.
- `2`: Run the swap-based Tabu search on the file.
- `3`: Run both algorithms on the contents of the given file.

The output of the program for options `1` and `2` is the maximum independent and its size as well as its running time.
The output for option `3` is two files `sbts-results.txt` and `approx-results.txt` with the average MIS size and running time for each file the was in the given file.
