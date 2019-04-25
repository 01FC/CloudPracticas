from PIL import Image
from resizeimage import resizeimage
import boto3
import sys

filename = "SMALLER"+sys.argv[1]
bucket_name = "buckets3practica2"
s3 = boto3.resource('s3')
p2_bucket = s3.Bucket(bucket_name)


p2_bucket.download_file(sys.argv[1], sys.argv[1])
with open(sys.argv[1], 'r+b') as f:
    with Image.open(f) as image:
        cover = resizeimage.resize_cover(image, [150, 150])
        cover.save(filename, image.format)
p2_bucket.upload_file(filename, filename)
