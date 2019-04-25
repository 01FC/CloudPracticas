FROM python:3.6
RUN pip install python-resize-image
RUN pip install boto3
WORKDIR /app
COPY app.py /app/
COPY resize.sh /app/
VOLUME ["/app/images"]