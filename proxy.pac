function FindProxyForURL(url, host)
 {
  if (dnsDomainIs(host, ".ccncsi.int")) 
      { return "PROXY 162.23.38.6:1998"; }
  if (shExpMatch(host, "*.slack-msgs.com") ) {                
        return "DIRECT";
    }
    if (shExpMatch(host, "*.slack.com") ) {                
        return "DIRECT";
    }
    if (shExpMatch(host, "*.slack-files.com") ) {                
        return "DIRECT";
    }
    if (shExpMatch(host, "*.slack-imgs.com") ) {                
        return "DIRECT";
    }
    if (shExpMatch(host, "*.slack-edge.com") ) {                
        return "DIRECT";
    }
    if (shExpMatch(host, "*.slack-core.com") ) {                
        return "DIRECT";
    }
    if (shExpMatch(host, "*.slack-redir.net") ) {                
        return "DIRECT";
    }
    if (shExpMatch(host, "wss-primary.slack.com") ) {                
        return "DIRECT";
    }
    if (shExpMatch(host, "wss-backup.slack.com") ) {                
        return "DIRECT";
    }
    if (shExpMatch(host, "wss-mobile.slack.com") ) {                
        return "DIRECT";
    }
  if (url.substring(0,19) == "http://lyncdiscover")
      { return "DIRECT"; }
  if (url.substring(0,20) == "https://lyncdiscover")
      { return "DIRECT"; }
  if (shExpMatch(host, "mgs1178.gs-vbs.admin.ch") || 
      shExpMatch(host, "mgs1300.gs-vbs.admin.ch") ||
      shExpMatch(host, "mgs1660.vbs.intra.admin.ch") ||
      shExpMatch(host, "logout"))
      { return "PROXY 131.102.8.121:80"; }
  if (isInNet(host, "192.168.100.0", "255.255.255.192"))
      { return "PROXY mgs0680.gs-vbs.admin.ch:80"; }
  if (isInNet(host, "131.102.190.76", "255.255.255.255"))
      { return "PROXY mgs1300.gs-vbs.admin.ch:8089"; }
  else if (shExpMatch(url, "https://portal.ejpd.admin.ch*") ||
      shExpMatch(url, "https://portal-int.ejpd.admin.ch*"))
      { return "PROXY localhost:8088"; }
  else if (shExpMatch(url, "http://portal.ejpd.admin.ch/mistravumdpsws*") ||
      shExpMatch(url, "http://portal-int.ejpd.admin.ch/mistravumdpsws*"))
      { return "PROXY localhost:8088"; }
  else if (!isResolvable(host))
      { return "PROXY lf00002a.adb.intra.admin.ch:8080"; }
  else if (isInNet(dnsResolve(host), "131.102.0.0", "255.255.0.0") ||
      isInNet(dnsResolve(host), "10.0.0.0", "255.0.0.0") ||
      isInNet(dnsResolve(host), "162.23.0.0", "255.255.0.0") ||
      isInNet(dnsResolve(host), "192.168.0.0", "255.255.0.0") ||
      isInNet(dnsResolve(host), "172.16.0.0", "255.240.0.0") ||
      isInNet(dnsResolve(host), "193.5.217.0", "255.255.255.0") ||
      isInNet(dnsResolve(host), "193.5.221.0", "255.255.255.0") ||
      isInNet(dnsResolve(host), "193.5.223.64", "255.255.255.192") ||
      isInNet(dnsResolve(host), "193.5.223.128", "255.255.255.192") ||
      isInNet(dnsResolve(host), "194.11.240.0", "255.255.255.0") ||
	  isInNet(dnsResolve(host), "194.11.241.128", "255.255.255.128") ||
      isInNet(dnsResolve(host), "62.62.0.0", "255.255.128.0") ||
      isInNet(dnsResolve(host), "62.2.176.68", "255.255.255.255") ||
      isInNet(dnsResolve(host), "145.250.120.0", "255.255.255.0") ||
      isInNet(dnsResolve(host), "192.112.254.44", "255.255.255.255") ||
      isInNet(dnsResolve(host), "192.112.254.45", "255.255.255.255") ||
      isInNet(dnsResolve(host), "193.72.145.20", "255.255.255.255") ||
      isInNet(dnsResolve(host), "193.72.145.21", "255.255.255.255") ||
      isInNet(dnsResolve(host), "195.144.18.214", "255.255.255.255") ||
      isInNet(dnsResolve(host), "195.144.18.215", "255.255.255.255") ||
      isInNet(dnsResolve(host), "195.144.19.192", "255.255.255.192") ||
      isInNet(dnsResolve(host), "127.0.0.0", "255.0.0.0") ||
      shExpMatch(url, "https://localhost*") ||
      shExpMatch(url, "http://localhost*") ||
      shExpMatch(url, "http://pd.clipping.ch*") ||
      shExpMatch(url, "http://web.it-logix.ch*") ||
      shExpMatch(url, "https://cs.directnet.com*") ||
      shExpMatch(url, "https://jpmorganaccess.com*") ||
      shExpMatch(url, "https://access.jpmorgan.com*") ||
      shExpMatch(url, "https://tssportal.jpmorgan.com*") ||
      shExpMatch(url, "https://webgate.ec.europa.eu*") ||
      shExpMatch(url, "https://webgate.acceptance.ec.europa.eu*") ||
      shExpMatch(url, "https://cadas.skyguide.ch*") ||
      shExpMatch(url, "https://cadas-valid.skyguide.ch*") ||
      shExpMatch(url, "https://www.taxmeonline.ch*") ||
      shExpMatch(url, "https://www.shab.ch*") ||
      shExpMatch(url, "www.simap.ch*") ||
      shExpMatch(url, "https://newwww.simap.ch*") ||
      shExpMatch(url, "https://asterix.bedag.ch*") ||
      shExpMatch(url, "https://obelix.bedag.ch*") ||
      shExpMatch(url, "https://cagbve.portal.be.ch*") ||
      shExpMatch(url, "https://cag.portal.be.ch*") ||
      shExpMatch(url, "https://citrixdch.unilabs.ch*") ||
      shExpMatch(url, "http://iddprod2.tkfweb.com*") ||
      shExpMatch(url, "https://*x.dhsb.ch*") ||
      shExpMatch(url, "https://*.jpmorgan.com/*") ||
      shExpMatch(url, "http://license.mapinfo.com*") ||
      shExpMatch(url, "http://*.unesco.org/*"))
      { return "DIRECT"; }

  else if (isResolvable("lf00002a.adb.intra.admin.ch"))
      { return "PROXY lf00002a.adb.intra.admin.ch:8080"; }
  else
      { return "DIRECT"; }}