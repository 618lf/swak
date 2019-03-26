const _Reg = new RegExp("[`~!@#$^&*()=|{}':;',\\[\\].<>/?~！@#￥……&*（）——|{}【】‘；：”“'。，、？]");

function strSimilarity2Number(s, t) {
  var n = s.length, m = t.length, d = [];
  var i, j, s_i, t_j, cost;
  if (n == 0) return m;
  if (m == 0) return n;
  for (i = 0; i <= n; i++) {
    d[i] = [];
    d[i][0] = i;
  }
  for (j = 0; j <= m; j++) {
    d[0][j] = j;
  }
  for (i = 1; i <= n; i++) {
    s_i = s.charAt(i - 1);
    for (j = 1; j <= m; j++) {
      t_j = t.charAt(j - 1);
      if (s_i == t_j) {
        cost = 0;
      } else {
        cost = 1;
      }
      d[i][j] = Minimum(d[i - 1][j] + 1, d[i][j - 1] + 1, d[i - 1][j - 1] + cost);
    }
  }
  return d[n][m];
}


function Minimum(a, b, c) {
  return a < b ? (a < c ? a : c) : (b < c ? b : c);
}

function strSimilarity2Percent(s, t) {
  var l = s.length > t.length ? s.length : t.length;
  var d = strSimilarity2Number(s, t);
  return (1 - d / l).toFixed(4);
}

function likeByPercent(s, t) {
  return strSimilarity2Percent(s, t);
}

function likeByRegex(s, t) {
  if (_Reg.test(t)) {
    var reg = new RegExp("^" + t + "$", "g");
    if (reg.test(s)) {
      return true;
    }
  }
  return false;
}


function likes(s, ss) {
  var scores = [];
  var _ss = ss.split(',');
  _ss.forEach((x) => {
    if (!x) {
      scores.push(0)
    } else if (likeByRegex(s, x)) {
      scores.push(1)
    } else {
      scores.push(likeByPercent(s, x))
    }
  })
  scores.sort(function (v1, v2) {
    if (v1 < v2) {
      return 1;
    } else if (v1 > v2) {
      return -1;
    } else {
      return 0;
    }
  });
  return scores[0];
}

/**
 * check
 * @type {{check: Similarity.check}}
 */
const Similarity = {
  like: (str1, str2) => {
    return likes(str1, str2);
  }
}

export default Similarity