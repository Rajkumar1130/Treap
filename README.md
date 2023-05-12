# Treap
Treap is based on AVL tree algorithm. In this project I try to do insert some keys with priority. Then split the treap and rejoin them and also remove function.


For Treap file.

The first(Treap.java) part of the project involves implementing the following functions of Treap. Note that key can be any type that
is from an ordered set (e.g. string, integer etc). Priority can be any positive number.
(a) insertWithPriority(key,priority) -- insert a given key with the specified priority if key does not exist else replace the
priority value of the key with the new priority.
(b) insert(key) -- insert/replace a given key with random priority value
(c) remove(key) – remove key and return priority value associated with the given key if it exists, else throw exception
(d ) find(key) – find priority value associated with the given key if it exists, else return 0.
(e) split(key) – given a key, return a treap containing keys larger than “key” and also the treap object on which the split
operation is called will be modified to contain only the keys smaller than or equal to “key”
(f) join(T) – given a treap T with keys greater than any of the keys in the treap object on which the operation is called,
modify that treap to include also the keys in T.
(g) size() – get the number of keys in the treap.



For TreapPerformanceAnalyzer file.

Second(TreapPerformanceAnalyzer.java) part of the project requires you to write a program to test the performance of this implementation for find,
insert, remove, split and join operations. Specifically, you should generate randomly a sequence of unique integers in a
range (say 1 to 100000) to be inserted as keys starting from an empty treap. You should intersperse insert operations
with equal number of find, remove, split and join operations in the sequence. You should make sure there will be
sufficient successful and unsuccessful searches and valid remove operations.
