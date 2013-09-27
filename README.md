squabble
========

Word game in java using GWT

Getting started
----------------
    git clone https://github.com/davidmoten/sowpods.git
    cd sowpods
    mvn clean install
    cd ..
    git clone https://github.com/davidmoten/squabble.git
    cd squabble
    mvn clean install
    cd squabble
    mvn jetty:run

Go to http://localhost:9292/squabble

How to play squabble without a computer
-----------------------------------------
This game rocks. I've been playing it since about 2000 and we kind of have rules that work and don't get us into too much trouble unless we're playing with lawyers!

Take a set of scrabble letters and turn them face down on a table between 2 or more players.

Each player takes a turn to flip over a letter.

As soon as a word of at least 3 letters is visible the first player to say the word collects the letters and places them as that word in front of them. 

At any time a player can say an anagram using the visible single letters and zero or more of any of the complete words on the table. You cannot take only part of a word, you must use all the other letters.

If two players say a word at the same time then:
* if one word contains the other word then the player with the longest word wins
* otherwise no-one gets a word but the next time a letter turns up that participates in that word it may be said again

A word is not allowed if 
* it is not in the dictionary nominated as the authority for the game
* the word does not change the root of any word in that word's anagram history. So for instance red -> read -> dear -> reads is not valid because reads has the same root as read which was in the history of the current value of dear.

If you get into trouble with this rule about changing the root then try a relaxation that has strict inarguable rules:
* You cannot make a word from an existing word (or any word in that word's history) using:

```Suffixes r, s, er, es, d, ed, ing, or n
Prefix re```

If you succeed in getting a word it is then your turn to turn a letter. You have a chance to slow the game down here if you want to. Mind you if someone yells out a word before you turn too bad, off goes the game again!

The player with the most words when there are no more letters to turn over wins.

If a player is stronger or more experienced then that player should level the game a bit by imposing a minimum of say 4, 5 or 6 letters on themselves. 

Variants
--------------
Try playing with a minimum of 4,5,6,7,8,9, even 10 letters!


