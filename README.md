# Merge algorithm exercises

Whenever we are performing a query with more than one word, we have to perform
some kind of combination of the results for documents that do or do not 
contain each word. 

This exercise works through some of the cases that it is good to understand.

In the first class, we spent almost all the time talking about Google....
A large search engine has thousands of machines and can keep a ton of stuff
in memory. But we should also consider small search systems, such as Spotlight
search on your Mac or the equivalent Windows Search for Windows.

*1.*
Do standard Linux distribution provide a search engine for your computer?
Your Linux geek friend says that it's easy and that if you want to search for
files with "apple" and "computer" in them, all you need to do is type:
```bash
comm -12  <(grep -Riwl "apple" /) <(grep -Riwl "computer" /)
```
Is that a good solution?

If we want to provide a good text search system on a small machine, then:
 1. For speed we need to index.
 2. For a good trade-off on memory use/time efficiency, we probably keep the dictionary in memory
   but we need to keep all the postings lists on disk
 3. For acceptable speed when working with postings lists on disk, we need to have an algorithm
   that streams postings from disk and just iterates once forward through the 
   postings list. A hard disk does not support efficient random access.
 4. Algorithms that do this for two or more lists simultaneously are referred to 
   as "merge algorithms". The name is maybe misleading, since we sometimes, e.g.,
   intersect rather than merging lists, but the name is traditional.
 5. The secret to such algorithms working is consistently *sorting* postings lists.
   
*2.*
Let's first write a simple routine to do an "AND" query - we intersect two postings lists.
We've provided in `Intersect.java` enough of a skeleton for some code that loads postings
lists and tries to intersect pairs of them. Here's one potential solution:

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

*3.* 
Let's now write a solution using a merge algorithm.
It'd be by far the best if you can just write your own merge algorithm for 
postings list intersection from first principles,
but if you can't remember what that's about, you could look at Figure 1.6
of the textbook. Check that your solution works on our test cases.

*4.* 
Suppose we then wanted to do an "OR" algorithm to more truly "merge" postings lists
How would you modify your code in `Intersect.java` to do an "OR". 
Try it out in `Or.java`.

*5.*
Most search engines, including Google support a negation or "NOT" operation.
For instance, search on Google for [space]. (That is search for the stuff inside the square brackets.)
For negation, you precede a word with "-". Try searching on Google for [space -astronomy]. See how
the results change. What should happen if you just search for a negation like [-astronomy]?  What 
does happen? Is there a good reason why things might work the way they do?

*6.* 
Can we write an efficient merge algorithm for "AND NOT" queries?
 Try it out in `AndNot.java`.
 
*7.* 
