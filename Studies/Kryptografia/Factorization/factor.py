from math import floor
from fractions import gcd

def factorize(n):
	c = int(floor(pow(n, 0.25)))
	f = {}
	for i in xrange(c):
		f[i] = 1
		jmin = 1 + i*c
		jmax = jmin + c
		for j in xrange(jmin, jmax):
			f[i] = f[i]*j % n
	results = {}
	for i in xrange(c):
		factor = gcd(f[i], n)
		if factor != 1:
			results[i] = factor
	return results

print factorize(100009)