  1 #ifndef __MSTDIO_H__
  2 #define __MSTDIO_H__
  3 #include <sys/types.h>
  4 
  5 #define MEOF    -1
  6 
  7 enum mode{READ, WRITE, APPEND}
  8 
  9 typedef struct
 10 {
 11     int     _fd;// 文件描述符
 12     char    *_buffer;
 13     char    *_nextc;
 14     int     _mode;
 15     off_t   _left; // 计数器
 16 }MFILE;
 17 
 18 extern MFILE* mfopen(const char * const pathname, const char * const mode);
 19 extern int mfclose(MFILE *fp);
 20 extern void mfflush(MFILE *fp);
 21 extern MFILE* mfdopen(int fd, const char * const mode);
 22 
 23 extern int mfgetc(MFILE *fp);
 24 extern int mfputc(int character, MFILE *fp);
 25 extern int mungetc(int character, MFILE *fp);
 26 extern char* mfgets(char *buffer,int size,  MFILE *fp);
 27 extern int mfputs(char *buffer, MFILE *fp);
 28 extern size_t mfread(void *buff, size_t size, size_t counter, MFILE *fp);
 29 extern size_t mfwrite(void *buff, size_t size, size_t counter, MFILE *fp);

