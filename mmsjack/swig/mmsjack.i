%module mmsjack

%{
#include "jack.h"
%}

/* This tells SWIG to treat char ** as a special case when used as a parameter
   in a function call */
%typemap(in) char ** (jint size) {
    int i = 0;
    size = (*jenv)->GetArrayLength(jenv, $input);
    $1 = (char **) malloc((size+1)*sizeof(char *));
    /* make a copy of each string */
    for (i = 0; i<size; i++) {
        jstring j_string = (jstring)(*jenv)->GetObjectArrayElement(jenv, $input, i);
        const char * c_string = (*jenv)->GetStringUTFChars(jenv, j_string, 0);
        $1[i] = malloc((strlen(c_string)+1)*sizeof(char));
        strcpy($1[i], c_string);
        (*jenv)->ReleaseStringUTFChars(jenv, j_string, c_string);
        (*jenv)->DeleteLocalRef(jenv, j_string);
    }
    $1[i] = 0;
}

/* This cleans up the memory we malloc'd before the function call */
%typemap(freearg) char ** {
    int i;
    for (i=0; i<size$argnum-1; i++)
      free($1[i]);
    free($1);
}

/* This allows a C function to return a char ** as a Java String array */
%typemap(out) char ** {
    int i;
    int len=0;
    jstring temp_string;
    const jclass clazz = (*jenv)->FindClass(jenv, "java/lang/String");

    while ($1[len]) len++;    
    jresult = (*jenv)->NewObjectArray(jenv, len, clazz, NULL);
    /* exception checking omitted */

    for (i=0; i<len; i++) {
      temp_string = (*jenv)->NewStringUTF(jenv, *result++);
      (*jenv)->SetObjectArrayElement(jenv, jresult, i, temp_string);
      (*jenv)->DeleteLocalRef(jenv, temp_string);
    }
}

/* These 3 typemaps tell SWIG what JNI and Java types to use */
%typemap(jni) char ** "jobjectArray"
%typemap(jtype) char ** "String[]"
%typemap(jstype) char ** "String[]"

/* These 2 typemaps handle the conversion of the jtype to jstype typemap type
   and vice versa */
%typemap(javain) char ** "$javainput"
%typemap(javaout) char ** {
    return $jnicall;
}

#define JACK_DEFAULT_AUDIO_TYPE "32 bit float mono audio"
#define JACK_DEFAULT_MIDI_TYPE "8 bit raw midi"

typedef int jack_nframes_t;

%include "enumtypeunsafe.swg"
enum JackPortFlags {
    JackPortIsInput = 0x1,
    JackPortIsOutput = 0x2,
    JackPortIsPhysical = 0x4,
    JackPortCanMonitor = 0x8,
    JackPortIsTerminal = 0x10,
    JackPortIsActive = 0x20
};

enum JackOptions {
    JackNullOption = 0x00,
    JackNoStartServer = 0x01,
    JackUseExactName = 0x02,
    JackServerName = 0x04,
    JackLoadName = 0x08,
    JackLoadInit = 0x10
};

%{
	typedef struct {
		jack_client_t * client;
		JNIEnv * 		env;
		jclass 			cls;
		jmethodID 		mid;
	    jobject 		obj_callback;
		int 			attached;
	} UserData;

	JavaVM * virtual_machine;

	int callback(jack_nframes_t nframes, void * arg) {
		int ret;
		UserData * data;
		
		printf("C::callback(%d)\n", nframes);
		
		data = (UserData *) arg;
		if (data == NULL) {
			printf("jack: there is no user data...\n"); fflush(stdout);
		}

		if (data->attached == 0) {
			data->attached = 1;
			(*virtual_machine)->AttachCurrentThreadAsDaemon(virtual_machine, (void **) &data->env, NULL);
			data->cls = (*data->env)->FindClass(data->env, "mmsjack/JackCallback");
			data->mid = (*data->env)->GetMethodID(data->env, data->cls, "callback", "(I)I");
		}

		ret = (int)(*data->env)->CallIntMethod(data->env, data->obj_callback, data->mid, nframes);

		return ret;
	}
%}

%inline %{
jobject test() {
	return NULL;
}
%}

extern jack_client_t * jack_client_new (const char *client_name);
extern int 	jack_client_close (jack_client_t *client);
extern jack_client_t* jack_client_open(const char * client_name, jack_options_t options, jack_status_t * status, ...);	
extern jack_nframes_t jack_get_sample_rate (jack_client_t *);
extern jack_port_t * jack_port_register (jack_client_t *client,
                                  const char *port_name,
                                  const char *port_type,
                                  unsigned long flags,
                                  unsigned long buffer_size);
extern int jack_activate (jack_client_t *client);
extern const char ** jack_get_ports (jack_client_t *,
                              const char *port_name_pattern,
                              const char *type_name_pattern,
                              unsigned long flags);
extern int jack_connect (jack_client_t *,
                  const char *source_port,
                  const char *destination_port);
extern int 	jack_disconnect (jack_client_t *, const char *source_port, const char *destination_port);
extern const char * jack_port_name (const jack_port_t *port);
extern void * jack_port_get_buffer (jack_port_t *, jack_nframes_t);
extern int 	jack_set_process_callback (jack_client_t *client, JackProcessCallback process_callback, void *arg);
extern jack_port_t* jack_port_by_name (jack_client_t *, const char * port_name);
extern jack_nframes_t 	jack_port_get_latency (jack_port_t *port);
extern jack_nframes_t 	jack_port_get_total_latency (jack_client_t *, jack_port_t *port);
extern jack_nframes_t 	jack_frames_since_cycle_start (const jack_client_t *);
extern jack_nframes_t 	jack_frame_time (const jack_client_t *);
extern jack_nframes_t 	jack_last_frame_time (const jack_client_t *client);
extern jack_time_t 	jack_frames_to_time (const jack_client_t *client, jack_nframes_t);
extern jack_nframes_t 	jack_time_to_frames (const jack_client_t *client, jack_time_t);
extern jack_time_t 	jack_get_time ();
int 	jack_port_unregister (jack_client_t *, jack_port_t *);