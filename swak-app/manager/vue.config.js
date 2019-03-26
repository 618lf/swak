module.exports = {
  publicPath: './', // ./ or /ptms or /ptms/ and route add /ptms/ and use history
  devServer: {
    port: 8081,
    disableHostCheck: true,   // That solved it
  },
}