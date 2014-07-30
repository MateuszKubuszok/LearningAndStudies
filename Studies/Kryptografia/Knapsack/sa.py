from lib import lll_reduction, modinv
from math import floor
import numpy

def priv_key(n):
  return [2**x for x in xrange(n)], 2**n, 3**int(floor(n/2))

def pub_key(p,q,r):
  return [i*r % q for i in p]

def encode(p,q,r,m):
  return sum([(p[i]*r % q)*(m[i] if i < len(m) else 0) for i in xrange(len(s))])

def decode(p,q,r,c):
  c2 = c*modinv(r,q) % q
  p.reverse()
  m = []
  for i in p:
    m.insert(0, 1 if i <= c2 else 0)
    c2 -= i if i <= c2 else 0
  return m

def attack(T, c):
  return [row[:-1] for row in lll_reduction(numpy.matrix([([(1 if i == j else 0) for j in xrange(len(T)+1)]) for i in xrange(len(T))] + [T + [-c]])).transpose().tolist() if len([x for x in row if x != 0 and x != 1]) == 0]

#p, q, r = [2, 5, 9, 21, 45, 103, 215, 450, 946], 2003, 1289
p, q, r = priv_key(10)
print "Private:", p, q, r
s = pub_key(p,q,r)
print "Public:", s
m = [1,0,1,1,0,0,1,1,1]
print "M:", m
c = encode(p,q,r,m)
print "C:", c
m2 = decode(p,q,r,c)
print "decoded C", m2
m3 = attack(s, c)
print "attacked C", m3