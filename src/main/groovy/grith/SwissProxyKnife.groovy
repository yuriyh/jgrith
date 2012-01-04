package grith
/**
 * @author Markus Binsteiner
 *
 */
public class SwissProxyKnife {

	private OptionAccessor opt
	GSSCredential cred 
	public SwissProxyKnife(String[] args) {
		cl = new CliBuilder()
		cl.h(longOpt:'help', 'usage information')
		cl.m(longOpt:PROXY_CREATION_MODE_OPTION, argName:'mode', args:1, required:true, 'The mode to create the proxy. Possible arguments: '+USE_LOCAL_MODE_PARAMETER+', '+CERTIFICATE_MODE_PARAMETER+', '+MYPROXY_MODE_PARAMETER+', '+SHIBBOLETH_MODE_PARAMETER+', '+SHIBBOLETH_LIST_MODE_PARAMETER)
		cl.l(longOpt:LIFETIME_PARAMETER, argName:'lifetime in hours', args:1, required:false, 'The lifetime of the proxy in hours. Default: 24')
		cl.u(longOpt:USERNAME_PARAMETER, argName:'username', args:1, required:false, 'The myproxy- or shibboleth-username used to get delegated proxy/shibboleth certificate')
		cl.i(longOpt:SHIBBOLETH_IDP_PARAMETER, argName:'idp name', args:1, required:false, 'The name of the idp to connect to')
		cl.s(longOpt:MYPROXY_PROXYNAME_PARAMETER, argName:'myproxy server', args:1, required:false, 'The myproxy server')
		cl.p(longOpt:MYPROXY_PORT_PARAMETER, argName:'myproxy port', args:1, required:false, 'The myproxy port')
		cl.a(longOpt:PROXY_OUTPUT_MODE_OPTION, argName:'action', args:1, required:false, 'What to do with the proxy. Use one of these options: '+LOCAL_OUTPUT_MODE_PARAMETER+', '+MYPROXY_OUTPUT_MODE_PARAMETER+', '+INFO_ACTION_PARAMETER+', '+VOMS_LIST_GROUPS)
		opt = cl.parse(args)

	}
	public static void main(String[] args) {
		
		SwissProxyKnife spk = new SwissProxyKnife(args)
		
	}
	
}