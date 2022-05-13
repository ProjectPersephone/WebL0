package com.example.demo;

public enum Nucleus {
//     T,        // from Hudak, et al. paper; eliminated
//     S,        // predicates could reduce to this??? check
     O_,        // combinator op // should this still be here?

     // need some way to indicate blocks; presently done
     // with the Line/Lines class
     block_,        // begins with indentation; every eol should add an S to a block.
                    // including top-level scope.
     block_end_,    // block ends with dedent.

     I,
     YOU,
     SOMEONE,
     SOMETHING,
/*
     BODY,
     PEOPLE,
     KIND,
     PART,
     WORDS,
*/
     THIS,
/*
     THE_SAME,
     OTHER,
     ONE,
     TWO,
     MUCH_MANY,
     ALL,
     SOME,  // can be wh-word "how much" and then SOME as unbound/bound var?
     LITTLE_FEW,
*/

/*
     TIME_WHEN, // SOMETIME
     NOW,
     MOMENT,
     FOR_SOME_TIME,  // (for) some time, could be "a time" or "for some time"?
     A_LONG_TIME,
     A_SHORT_TIME,
     BEFORE,
     AFTER,
     */

     WANT,
     DONT_WANT,
/*
     FEEL, */
     DO,

     SAY,
     KNOW,
/*
     SEE,
     HEAR,
     THINK,
*/

     HAPPEN,
     BE,    // copula
     LIVE,
     DIE,
/*
     THERE_IS,
     BE_SOMEWHERE,
*/

/*
     IS_MINE,
     MOVE,
     TOUCH,
     INSIDE,
*/


     SOMEWHERE,
/*
     HERE,
     ABOVE,
     BELOW,
     ON_ONE_SIDE, // (on) one side, quasisubstantive?
     NEAR,
     FAR,
*/


     NOT,
     CAN,
/*
     BECAUSE,
*/
     IF,
     MAYBE,
/*
     LIKE,     // ~as~way(~how)
     VERY,
     MORE,
*/

/*
     SMALL,
     BIG,
*/
     BAD,
     GOOD,
     TRUE,
}
