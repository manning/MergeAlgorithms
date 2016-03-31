# Merge algorithm exercises

Whenever we are performing a query with more than one word, we have to perform
some kind of combination of the results for documents that do or do not 
contain each word. 

This exercise works through some of the cases that it is good to understand.

In the first class, we spent almost all the time talking about Google....
A large search engine has thousands of machines and can keep a ton of stuff
in memory. But we should also consider small search systems, such as Spotlight
search on your Mac or the equivalent Windows Search for Windows.

## 1.

Do standard Linux distribution provide a search engine for your computer?
Your Linux geek friend says that it's easy and that if you want to search for
files with "apple" and "computer" in them, all you need to do is type:
```bash
comm -12  <(grep -Riwl "apple" /) <(grep -Riwl "computer" /)
```
Is that a good solution?

If we want to provide a good text search system on a small machine, then:
 - For speed we need to index.
 - For a good trade-off on memory use/time efficiency, we probably keep the dictionary in memory
   but we need to keep all the postings lists on disk
 - For acceptable speed when working with postings, we need to have an algorithm
   that streams postings from disk (and just iterates once forward through the 
   postings list).
 - Algorithms that do this for two or more lists simultaneously are referred to 
   as "merge algorithms". The name is maybe misleading, since we sometimes, e.g.,
   intersect rather than merging lists, but the name is traditional.
   
## 2.

