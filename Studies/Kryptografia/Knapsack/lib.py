# This program is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with this program. If not, see <http://www.gnu.org/licenses/>.
#
# Based on the implementation of liblll from https://github.com/kutio/liblll
# Modified by trdean to use numpy.  Got rid of use of Fraction...computing
# gcd on each call was taking most of time to compute LLL (now using floats)

import numpy
import math

# gram-schmidt algorithm
def gram_schmidt(g, m, mu, B):

  row = len(m)

  for i in xrange(row):
    # bi* = bi
    b_i = g[:,i]
    b_i_star = b_i
    m[:, i] = b_i_star

    for j in xrange(i):
      # u[i][j] = (bi, bj*)/Bj
      b_j_star = m[:, j]
      b_i = g[:, i]
      B[j] = numpy.vdot(b_j_star, b_j_star)
      mu[i,j] = numpy.vdot(b_i, b_j_star)[0,0]/ float(B[j,0])
      b_i_star = b_i_star - b_j_star*mu[i,j]
      m[:, i] = b_i_star

    b_i_star = m[:, i]
    # B[i] = (bi*, bi*)
    B[i] = numpy.vdot(b_i_star, b_i_star)
 

# reduce
def reduce(g, mu, k, l):
  row = len(g)
  col = len(g[0])

  if math.fabs(mu[k,l]) > 0.5:
    r = int(round(mu[k,l]))
    b_k = g[:, k]
    b_l = g[:, l]
    # bk = bk - r*bl
    g[:, k] = b_k - b_l*r

    for j in xrange(l):
      # u[k][j] = u[k][j] - r*u[l][j]
      mu[k,j] = mu[k,j] - r*mu[l,j]

    # u[k][l] = u[k][l] - r
    mu[k,l] = mu[k,l] - r


# Main LLL Reduction
def lll_reduction(n, lc=0.75):

  row = n.shape[0]
  col = n.shape[1]

  m = numpy.matrix(numpy.zeros(shape=(row,col)),int).astype(object)
  mu = numpy.matrix(numpy.zeros(shape=(row,col)))
  g = n
  B = numpy.matrix(numpy.zeros(shape=(row,1)),int).astype(object)

  gram_schmidt(g, m, mu, B)

  #k = 2
  k = 1

  while 1:
    # 1 - perform (*) for l = k - 1
    reduce(g, mu, k, k-1)

    # lovasz condition
    if B[k,0] < (lc - mu[k,k-1]*mu[k,k-1])*B[k-1,0]:
      # 2
      # u = u[k][k-1]
      u = mu[k,k-1]

      # B = Bk + u^2*Bk-1
      big_B = float(B[k,0] + (u*u) * B[k-1,0])

      # mu[k][k-1] = u * B[k-1] / B
      mu[k,k-1] = u*B[k-1,0]/ big_B
      # Bk = Bk-1 * Bk / B
      B[k,0] = B[k-1,0] * B[k,0]/ big_B

      # Bk-1 = B
      B[k-1,0] = big_B

      # exchange bk and bk-1
      tmp = numpy.matrix(g[:, k])
      g[:,k] = g[:, k-1]
      g[:,k-1] = tmp
      
      # for j = 0 .. k-2
      for j in xrange(k-1):
        save = mu[k-1,j]
        mu[k-1,j] = mu[k,j]
        mu[k,j] = save

      for i in xrange(k+1, row):
        save = mu[i,k-1]
        mu[i,k-1] = mu[k,k-1]*mu[i,k-1] + mu[i,k] - u*mu[i,k]*mu[k,k-1]
        mu[i,k] = save - u*mu[i,k]

      # if k > 2
      if k > 1:
        k = k - 1

    else:
      for l in xrange(k-2, -1, -1):
        reduce(g, mu, k, l)

      if k == row-1:
        return g

      k = k + 1

# Checks if in form of LLL reduction
def islll(n, lc=0.75):

  row = n.shape[0]
  col = n.shape[1]

  m = numpy.matrix(numpy.zeros(shape=(row,col)),int).astype(object)
  mu = numpy.matrix(numpy.zeros(shape=(row,col)))
  B = numpy.matrix(numpy.zeros(shape=(row,1)),int).astype(object)

  gram_schmidt(n, m, mu, B)

  for i in xrange(row):
    for j in xrange(i):
      if math.fabs(mu[i,j]) > 0.5:
        return False

  for k in xrange(1, row):
    if B[k] < (lc - mu[k,k-1]*mu[k,k-1])*B[k-1,0]:
      return False

  return True

  
def egcd(a, b):
    x,y, u,v = 0,1, 1,0
    while a != 0:
        q,r = b//a,b%a; m,n = x-u*q,y-v*q
        b,a, x,y, u,v = a,r, u,v, m,n
    return b, x, y

def modinv(a, m):
    g, x, y = egcd(a, m)
    if g != 1:
        return None
    else:
        return x % m