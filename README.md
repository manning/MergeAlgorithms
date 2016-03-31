# CS276 merge algorithm exercises

Whenever we are performing a query with more than one word, we have to perform
some kind of combination of the results for documents that do or do not 
contain each word. 

This exercise works through some of the cases that you should understand.

In the first class, we spent almost all the time talking about Google....
A large search engine has thousands of machines and can keep a ton of stuff
in memory. But we should also consider small search systems, such as Spotlight
search on your Mac or the equivalent Windows Search for Windows.

**1.**
Do standard Linux distributions provide a search engine for your computer?
Your Linux geek friend says that it's easy and that if you want to search for
files with "apple" and "computer" in them, all you need to do is type:
```bash
comm -12  <(grep -Riwl "apple" /) <(grep -Riwl "computer" /)
```
Is that a good solution?

If we want to provide a good text search system on a small machine:
 1. For speed, we need to index.
 2. For a good trade-off on memory use/time efficiency, we probably keep the dictionary in memory
   but we need to keep all the postings lists on disk
 3. For acceptable speed and memory use when working with postings lists on disk, we need to have an algorithm
   that streams postings from disk and just iterates once forward through the 
   postings list. A hard disk does not support efficient random access.
   Algorithms that do this for two or more lists simultaneously are referred to 
   as "merge algorithms". The name is maybe misleading, since we sometimes, e.g.,
   intersect rather than merging lists, but the name is traditional.
 4. The secret to such algorithms being efficient is consistently *sorting* postings lists.
In this exercise, our postings lists are actually built in memory for simplicity, 
but we want to write algorithms that support this model of efficiently streaming postings lists 
from disk.

### Document-level indices

**2.**
Let's first write a simple routine to do an "AND" query – we intersect two postings lists.
We've provided in `Intersect.java` (in `src`) a skeleton for some code that loads postings
lists and tries to intersect pairs of them. Postings lists are semicolon-separated lists
of document IDs, which you can pass in on the command-line or you can use our test cases.
To get this code in Eclipse, do `File|Import` choose `Git|Projects from Git`, press `Next`,
`Clone URI` then `Next`, then enter the HTTPS URI on this page, and `Next`, `Next`, `Next`, 
go with `Import existing projects`, `Next` and `Finish` – and you should be all ready to go!
(The code uses the Java 7 diamond operator – if you last used Java in CS106A, then you do
need to update to a more modern version of Eclipse.)

Here's one potential solution:

```
  static List<Posting> listFromIterator(Iterator<Posting> iter) {
    List<Posting> list = new ArrayList<Posting>();
    Posting p;
    while ((p = popNextOrNull(iter)) != null) {
      list.add(p);
    }
    return list;
  }

  static List<Integer> intersect(Iterator<Posting> p1, Iterator<Posting> p2) {
    List<Integer> answer = new ArrayList<Integer>();

    List<Posting> lp1 = listFromIterator(p1);
    List<Posting> lp2 = listFromIterator(p2);
    for (Posting posting1 : lp1) {
      for (Posting posting2 : lp2) {
        if (posting1.docID == posting2.docID) {
          answer.add(posting1.docID);
        }
      }
    }

    return answer;
  }
```

Is it a good solution in terms of the criteria above? Why or why not?

**3.** 
Let's now write a solution using a merge algorithm.
It'd be by far the best if you can just write your own merge algorithm for 
postings list intersection from first principles!
However, if you can't remember what that's about, you could look at Figure 1.6
of the textbook. Check that your solution works on our test cases.

**4.** 
Suppose we then wanted to do an "OR" algorithm to more truly "merge" postings lists
How would you modify your code in `Intersect.java` to do an "OR". 
Try it out in `Or.java`.
 
*~~~ If you have extra time before we move to positional indices, you can also do __7__ and __8__. ~~~*

### Positional indices

It's pretty standard these days that an IR system can efficiently answer not only finding documents that
contain multiple words but requiring that those words occur close by. The simplest case is
phrase queries where we require them to be adjacent and ordered like ["machine learning"].
A more complex form is "WITHIN k" queries which we write "/k".  For example, 
the query [student /3 drunk] would match a document saying either "drunk student" or 
"a student who is drunk" but not "the student said that the faculty member was drunk".
Note that the algorithm should return **all** matches in the document, so that if document 7 is
"a drunk student who is drunk", then the query [drunk /3 student] should return two maches for 
this document: (7, 2, 3) and (7, 6, 3).

**5.** 
We will augment our postings lists with the positions of each token within each document,
numbering them as token 1, token 2, etc. After each document ID, there will now be a colon
and then a comma-separated list of positions. How can we extend our postings list merge
algorithms to work with positional postings lists? Try to work out an algorithm that will do that.
We've provided a skeleton in the file `PositionalWithinK.java`. Try to write something that 
passes the test cases provided there.  (**Note:** There is some code in Introduction to Information Retrieval
for this algorithm, but we're _really_ wanting you to try to write it by yourself. This education thing is
all about _learning_, right? Pretend that you're practicing for your Google coding interview!)

**6.**
Our document-level merge algorithm had some important properties. It worked using a single forward pass
through a postings list, so it was suitable for applying to postings lists streamed from disk, so it could be both time efficient
(linear in the length of the postings lists) and space efficient (the memory required does not depend on the size of the postings
lists, since in the streaming scenario, you can just read and refill as needed a sliding buffer over the postings list,
like standard buffered IO.  (As implemented in our sample code, space need only grow with the size of the set of matches.)
Can these properties be maintained for doing a WITHIN k merge?  We think they can!  If that's not true of your solution, 
try to rewrite it so that: (i) The algorithm makes a single always-forward pass through each postings list and (ii) 
The memory required does not depend on the size of the input postings list, not even the size of the postings list
for a single document (this is useful – some documents are very long!).


*~~~ If you have extra time ~~~*

**7.**
Most search engines, including Google support a negation or "NOT" operation.
For instance, search on Google for [space]. (That is search for the stuff inside the square brackets, the actual
word "space" as in dark and cold.)
For negation, you precede a word with "-". Try searching on Google for [space -astronomy]. See how
the results change. According to boolean logic, what should happen if you just search for a negation like [-astronomy]?  What 
does happen? Is there a good reason why things might work the way they do?

**8.**
Can we write an efficient merge algorithm for "AND NOT" queries?
 Try it out in `AndNot.java`.
