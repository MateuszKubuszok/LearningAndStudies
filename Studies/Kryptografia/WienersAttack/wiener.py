from random import *
from itertools import repeat
from functools import reduce
from time import time
from fractions import gcd
import random
from math import sqrt, log, floor

'''
Key generator
'''

def getPrime(n):
    """Get a n-bit pseudo-random prime"""
    def isProbablePrime(n, t = 7):
        """Miller-Rabin primality test"""
        def isComposite(a):
            """Check if n is composite"""
            if pow(a, d, n) == 1:
                return False
            for i in range(s):
                if pow(a, 2 ** i * d, n) == n - 1:
                    return False
            return True
     
        assert n > 0
        if n < 3:
            return [False, False, True][n]
        elif not n & 1:
            return False
        else:
            s, d = 0, n - 1
            while not d & 1:
                s += 1
                d >>= 1
        for _ in repeat(None, t):
            if isComposite(randrange(2, n)):
                return False
        return True   
     
    p = getrandbits(n)
    while not isProbablePrime(p):
        p = getrandbits(n)
    return p
 
def inv(p, q):
    """Multiplicative inverse"""
    def xgcd(x, y):
        """Extended Euclidean Algorithm"""
        s1, s0 = 0, 1
        t1, t0 = 1, 0
        while y:
            q = x // y
            x, y = y, x % y
            s1, s0 = s0 - q * s1, s1
            t1, t0 = t0 - q * t1, t1
        return x, s0, t0      
 
    s, t = xgcd(p, q)[0:2]
    assert s == 1
    if t < 0:
        t += q
    return t
 
def genRSA(p, q):
    """Generate public and private keys"""
    phi, mod = (p - 1) * (q - 1), p * q
    while(True):
        try:
            e = random.randint(3, mod-1)
            d = inv(e,phi)
            break
        except AssertionError:
            pass
    return (e,d,mod,p,q)


'''
RSA Cipcher
'''


def endecrypt(text, key, mod):
    aux1 = text
    assert aux1 < mod
    output = pow(aux1,key,mod)
    return output

def endecryptCRT(text, key, p, q):
    mod = p * q
    assert text < mod
    m1 = pow(text, key % (p - 1), p)
    m2 = pow(text, key % (q - 1), q)
    h = (inv(q, p) * (m1 - m2)) % p
    output = m2 + h * q
    return output


'''
Test
'''

t1 = time()
p = getPrime(256)
q = getPrime(256)
pk, sk, mod, p, q = genRSA(p, q)
t2 = time()
print 'Czas trwania generacji klucza: ', str(t2-t1)
print 'p:', p
print 'q:', q
print 'e:', pk
print 'd:', sk
print 'mod:', mod

mess = 242342344534535
print 'm:', mess

t1 = time()
ct1 = endecrypt(mess, pk, mod)
t2 = time()
print 'Czas trwania szyfrowania:', str(t2-t1)
print 'c:', ct1

t1 = time()
pt1 = endecryptCRT(ct1, sk, p, q)
t2 = time()
print pt1 == mess, pt1
print 'Czas trwania deszyfrowania CRT:', str(t2-t1)

t1 = time()
pt2 = endecrypt(ct1, sk, mod)
t2 = time()
print pt2 == mess, pt2
print 'Czas trwania deszyfrowania bez uzycia CRT:', str(t2-t1)
